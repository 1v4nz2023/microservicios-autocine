package com.grupo4.autocine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String fromEmail;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        
        String resetLink = frontendUrl + "?token=" + resetToken;
        String emailBody = String.format(
            "Hello,\n\n" +
            "You have requested to reset your password. Click the link below to reset it:\n\n" +
            "%s\n\n" +
            "This link will expire in 1 hour.\n\n" +
            "If you did not request this, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Autocine Team",
            resetLink
        );
        
        message.setText(emailBody);
        mailSender.send(message);
    }
} 