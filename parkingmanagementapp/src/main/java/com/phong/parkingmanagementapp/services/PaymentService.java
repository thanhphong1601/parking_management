/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import java.util.Map;

/**
 *
 * @author Admin
 */
public interface PaymentService {
    public String createPaymentUrl(long amount, String orderInfo, int ticketId) throws Exception;
    public String handleVnpayReturn(Map<String, String> params);
    public boolean validateSignature(Map<String, String> params, String secureHash);
}
