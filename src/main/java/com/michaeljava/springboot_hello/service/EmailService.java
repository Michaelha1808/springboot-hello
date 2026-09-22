package com.michaeljava.springboot_hello.service;

import com.michaeljava.springboot_hello.entity.mail.EmailEntity;

public interface EmailService {

    String sendTextEmail(EmailEntity email);
    String sendHtmlEmail(EmailEntity email);
    String sendMailAttachment(EmailEntity email);

}
