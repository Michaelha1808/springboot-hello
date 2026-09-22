package com.michaeljava.springboot_hello.service.impl;

import com.michaeljava.springboot_hello.entity.mail.EmailEntity;
import com.michaeljava.springboot_hello.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final String EMAIL_HOST = "haminhchi1808@gmail.com";

    @Autowired
    private JavaMailSender javaMailSender;
    @Override
    public String sendTextEmail(EmailEntity email) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email.getToEmail());
        message.setSubject(email.getSubject());
        message.setText(email.getMessageBody());
        message.setFrom(EMAIL_HOST);
        try{
            javaMailSender.send(message);
            System.out.println("Email send Successfully");
            return "Email send Successfully";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String sendHtmlEmail(EmailEntity email) {
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);

            helper.setFrom(EMAIL_HOST);
            helper.setTo(email.getToEmail());
            helper.setSubject(email.getSubject());
            helper.setText(email.getMessageBody(), true);

            javaMailSender.send(message);
            System.out.println("Email send HTML Successfully");
            return "Email send HTML Successfully";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String sendMailAttachment(EmailEntity email) {
        return "";
    }
}
