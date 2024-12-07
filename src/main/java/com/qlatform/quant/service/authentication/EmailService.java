package com.qlatform.quant.service.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    public void sendVerificationEmail(String to, String subject, String token) {
        String baseUrl = "http://localhost:8080";
        String verificationLink = baseUrl + "/v1/api/auth/verify-email?token=" + token;
        String content = createVerificationEmailContent(verificationLink);
        log.info("Sending verification email: {}", content);
    }

    private String createVerificationEmailContent(String verificationLink) {
        return String.format("""
            Welcome! Please verify your email address.
            Click the link below to verify your email:
            %s
            
            This link will expire in 24 hours.
            If you didn't create this account, please ignore this email.
            """, verificationLink);
    }
}