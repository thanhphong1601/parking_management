/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.dtos.MailBody;
import com.phong.parkingmanagementapp.models.Otp;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.repositories.UserRepository;
import com.phong.parkingmanagementapp.services.MailService;
import com.phong.parkingmanagementapp.services.OtpService;
import com.phong.parkingmanagementapp.services.UserService;
import java.time.Instant;
import java.util.Date;
import java.util.Random;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class ApiOtpController {

    private String annonymousEmail = "Anonymous@gmail.com";
    private final UserService userService;
    private final MailService mailService;
    private final OtpService otpService;

    public ApiOtpController(UserService userService, MailService mailService,
            OtpService otpService) {
        this.userService = userService;
        this.mailService = mailService;
        this.otpService = otpService;
    }

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable(value = "email") String email) {
        User currentUser = this.userService.findByEmail(email);
        if (currentUser == null) {
            currentUser = this.userService.findByEmail(annonymousEmail);
            //return new ResponseEntity<>("Email không chính xác", HttpStatus.CONFLICT);
        }

        Otp unusedOtp = this.otpService.findByUser(currentUser); //find if theres any unused otp user didnt use
        if (unusedOtp != null) {
            this.otpService.delete(unusedOtp);
        }

        int otpNumber = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("Đây là mã OTP dùng để xác thực: " + otpNumber + "\n"
                        + "Thời gian của mã OTP là 1 phút")
                .subject("Xác thực tài khoản")
                .build();

        Otp otp = Otp.builder()
                .otp(otpNumber)
                .expirationTime(new Date(System.currentTimeMillis() + 60 * 1000))
                .user(currentUser)
                .build();

        this.mailService.sendSimpleMail(mailBody);
        this.otpService.save(otp);

        return new ResponseEntity<>("Đã gửi email xác thực! Vui lòng kiểm tra email\n"
                + "Quá trình này có thể mất vài phút", HttpStatus.OK);
    }

    @PostMapping("/forgetPassword/verifyMail/{email}")
    public ResponseEntity<String> verifyEmailForForgetPassword(@PathVariable(value = "email") String email) {
        User currentUser = this.userService.findByEmail(email);
        
        if (currentUser == null) {      
            return new ResponseEntity<>("Email không chính xác", HttpStatus.CONFLICT);
        }

        Otp unusedOtp = this.otpService.findByUser(currentUser); //find if theres any unused otp user didnt use
        if (unusedOtp != null) {
            this.otpService.delete(unusedOtp);
        }

        int otpNumber = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("Đây là mã OTP dùng để đổi mật khẩu: " + otpNumber + "\n"
                + "Thời gian của mã OTP là 1 phút")
                .subject("Tìm lại mật khẩu")
                .build();

        Otp otp = Otp.builder()
                .otp(otpNumber)
                .expirationTime(new Date(System.currentTimeMillis() + 60 * 1000))
                .user(currentUser)
                .build();

        this.mailService.sendSimpleMail(mailBody);
        this.otpService.save(otp);

        return new ResponseEntity<>("Đã gửi email xác thực! Vui lòng kiểm tra email\n"
                + "Quá trình này có thể mất vài phút", HttpStatus.OK);
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable(value = "otp") int otp,
            @PathVariable(value = "email") String email) {
        User currentUser = this.userService.findByEmail(email);
        if (currentUser == null) {
            currentUser = this.userService.findByEmail(annonymousEmail);
            //return new ResponseEntity<>("Hãy cung cấp chính xác Email đã dùng để đăng ký tài khoản", HttpStatus.CONFLICT);
        }

        Otp currentOtp = this.otpService.findByOtpAndUser(otp, currentUser)
                .orElseThrow(() -> new RuntimeException("Hiện không có mã OTP nào dành cho mail: " + email));

        if (currentOtp.getExpirationTime().before(Date.from(Instant.now()))) {
            this.otpService.delete(currentOtp);
            return new ResponseEntity<>("Mã OTP đã hết hạn", HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("Xác thực OTP thành công");
    }

    public Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100000, 999999);
    }
}
