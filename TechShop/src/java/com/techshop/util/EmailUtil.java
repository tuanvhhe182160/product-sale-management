package com.techshop.util;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;

public class EmailUtil {
    private static final Properties config = new Properties();
    static{
        try (InputStream input = EmailUtil.class.getClassLoader().getResourceAsStream("com/techshop/conf/mail.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find mail.properties");
            } else {
                config.load(input);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendResetEmail(String toEmail, String resetLink) throws MessagingException {
        // 1. Cấu hình Properties cho SMTP (Sử dụng chuẩn Jakarta)
        Thread.currentThread().setContextClassLoader(EmailUtil.class.getClassLoader());
        Session session = Session.getInstance(config, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    config.getProperty("mail.smtp.user"), 
                    config.getProperty("mail.smtp.password")
                );
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getProperty("mail.smtp.user"), "TechShop Admin"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("[TechShop] Reset Password Request");

            String htmlContent = "<h3>Yêu cầu đặt lại mật khẩu</h3>"
                    + "<p>Click vào link sau để thực hiện: <a href='" + resetLink + "'>Reset Password</a></p>";
            
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(message);
            
        } catch (Exception e) {
            throw new MessagingException(e.getMessage());
        }       
    }
}