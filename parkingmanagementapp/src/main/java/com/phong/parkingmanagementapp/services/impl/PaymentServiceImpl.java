/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.configurations.VNPayConfig;
import com.phong.parkingmanagementapp.services.PaymentService;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    Dotenv env = Dotenv.load();
    String vnp_tmnCode = env.get("VNP_TMNCODE");
    String vnp_hashSecret = env.get("VNP_HASHSECRET");
    @Value("${vnp_ReturnUrl}")
    String vnp_returnUrl;
    @Value("${vnp_Url}")
    String vnp_url;

    @Override
    public String createPaymentUrl(long amount, String orderInfo, int ticketId) throws Exception {
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnp_tmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amount * 100));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", String.valueOf(new Date().getTime()));
        try {
            vnpParams.put("vnp_OrderInfo", URLEncoder.encode(orderInfo, StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PaymentServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
//        vnpParams.put("vnp_ReturnUrl", vnp_returnUrl);
        String fullReturnUrl = vnp_returnUrl + "?ticketId=" + ticketId;
        vnpParams.put("vnp_ReturnUrl", fullReturnUrl);
//        vnpParams.put("vnp_ReturnUrl", URLEncoder.encode(paymentConfig.getVnpReturnUrl(), StandardCharsets.UTF_8.toString()));

        String ipAddr = InetAddress.getLocalHost().getHostAddress();
        String hashedIpAddr = hmacSHA512(vnp_hashSecret, ipAddr);
        vnpParams.put("vnp_IpAddr", URLEncoder.encode(hashedIpAddr, StandardCharsets.UTF_8.toString()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        vnpParams.put("vnp_CreateDate", dateFormat.format(calendar.getTime()));

        calendar.add(Calendar.MINUTE, 15);
        vnpParams.put("vnp_ExpireDate", dateFormat.format(calendar.getTime()));

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = vnpParams.get(fieldName);
            if (value != null && !value.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                query.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                if (!fieldName.equals(fieldNames.get(fieldNames.size() - 1))) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String vnpSecureHash = hmacSHA512(vnp_hashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnpSecureHash);

        return vnp_url + "?" + query.toString();

    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac512.init(secretKey);
        byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

    @Override
    public String handleVnpayReturn(Map<String, String> params) {
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");

        if ("00".equals(responseCode)) {
            return "Payment successful with transaction number: " + transactionNo;
        } else {
            return "Payment failed!";
        }
    }

    @Override
    public boolean validateSignature(Map<String, String> params, String secureHash) {
        params.remove("vnp_SecureHash");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);
            if ((value != null) && (value.length() > 0)) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                hashData.append('&');
            }
        }

        String data = hashData.length() > 0 ? hashData.substring(0, hashData.length() - 1) : "";

        try {
            String calculatedHash = hmacSHA512(vnp_hashSecret, data);
            return calculatedHash.equals(secureHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
