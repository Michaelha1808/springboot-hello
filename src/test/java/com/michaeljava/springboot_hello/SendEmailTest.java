package com.michaeljava.springboot_hello;

import com.michaeljava.springboot_hello.util.EmailSenderUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@SpringBootTest
public class SendEmailTest {
    @Autowired
    private EmailSenderUtil emailSenderUtil;

    @Test
    void  sendTextEmail(){
        String to = "michaelha@gmail.com";
        String subject = "Test OPT Simple";
        String content = "Hello";

        emailSenderUtil.sendTextEmail(to, subject, content);
    }


    @Test
    void  sendHTMLEmail() throws IOException {
        String to = "michaelha@gmail.com";
        String subject = "Test OPT HTML";
        String content = "OPT is 123";

        Resource resource = new ClassPathResource("/templates/email/otp_auth.html");
        String htmlContent = new String(resource.getInputStream().readAllBytes());
        emailSenderUtil.sendHtmlEmail(to, subject, content);
    }
}
