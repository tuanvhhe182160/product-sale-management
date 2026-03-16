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

            String htmlContent = "<h3>Request Reset Password</h3>"
                    + "<p>Click vào link sau: <a href='" + resetLink + "'>Reset Password</a></p>";
            
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(message);
            
        } catch (Exception e) {
            throw new MessagingException(e.getMessage());
        }       
    }
    
    // Bổ sung vào EmailUtil.java
    public static void sendWarrantyStatusEmail(String toEmail, String customerName, String requestCode, String newStatus, String resolutionNote) {
        if (toEmail == null || toEmail.trim().isEmpty()) return; // Bỏ qua nếu khách không có email
        
        // Tạo luồng riêng (Thread) để gửi mail không làm chậm tốc độ phản hồi của web
        new Thread(() -> {
            try {
                Thread.currentThread().setContextClassLoader(EmailUtil.class.getClassLoader());
                Session session = Session.getInstance(config, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(config.getProperty("mail.smtp.user"), config.getProperty("mail.smtp.password"));
                    }
                });
                
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(config.getProperty("mail.smtp.user"), "TechShop Warranty Center"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("[TechShop] Cập nhật trạng thái bảo hành #" + requestCode);

                // Dịch trạng thái sang tiếng Việt
                String statusVN = "Đang xử lý"; // Mặc định
                try {
                    com.techshop.model.WarrantyStatus statusEnum = com.techshop.model.WarrantyStatus.valueOf(newStatus);
                    switch (statusEnum) {
                        case PENDING: statusVN = "Chờ tiếp nhận"; break;
                        case IN_PROGRESS: statusVN = "Đang xử lý / Sửa chữa"; break;
                        case COMPLETED: statusVN = "Đã hoàn thành / Chờ trao trả"; break;
                        case REJECTED: statusVN = "Từ chối bảo hành"; break;
                        case CANCELLED: statusVN = "Đã hủy"; break;
                    }
                } catch (IllegalArgumentException e) {
                    // Bỏ qua nếu newStatus không khớp Enum
                }

                StringBuilder html = new StringBuilder();
                html.append("<h3>Xin chào ").append(customerName).append(",</h3>");
                html.append("<p>Yêu cầu bảo hành mã <strong>").append(requestCode).append("</strong> của bạn vừa được cập nhật trạng thái.</p>");
                html.append("<p>Trạng thái hiện tại: <strong style='color:blue;'>").append(statusVN).append("</strong></p>");
                
                if (resolutionNote != null && !resolutionNote.isEmpty()) {
                    html.append("<p>Ghi chú từ Kỹ thuật viên: <em>").append(resolutionNote).append("</em></p>");
                }
                html.append("<p>Cảm ơn bạn đã tin tưởng dịch vụ của TechShop!</p>");
                
                message.setContent(html.toString(), "text/html; charset=UTF-8");
                Transport.send(message);
                System.out.println("Email bảo hành đã gửi tới: " + toEmail);
                
            } catch (Exception e) {
                System.err.println("Lỗi gửi email bảo hành: " + e.getMessage());
            }
        }).start();
    }
    
    // Dùng khi CS vừa tạo phiếu thành công
    public static void sendNewWarrantyEmail(String toEmail, String customerName, String requestCode, String productName, String imei, String issueDescription) {
        if (toEmail == null || toEmail.trim().isEmpty()) return; 

        new Thread(() -> {
            try {
                Thread.currentThread().setContextClassLoader(EmailUtil.class.getClassLoader());
                Session session = Session.getInstance(config, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(config.getProperty("mail.smtp.user"), config.getProperty("mail.smtp.password"));
                    }
                });
                
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(config.getProperty("mail.smtp.user"), "TechShop Warranty Center"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("[TechShop] Tiếp nhận yêu cầu bảo hành máy " + productName);

                StringBuilder html = new StringBuilder();
                html.append("<h3>Xin chào ").append(customerName).append(",</h3>");
                html.append("<p>Hệ thống TechShop đã ghi nhận yêu cầu bảo hành của bạn với thông tin như sau:</p>");
                html.append("<ul>");
                html.append("<li><strong>Mã phiếu:</strong> <span style='color:blue; font-size:16px;'>").append(requestCode).append("</span></li>");
                html.append("<li><strong>Sản phẩm:</strong> ").append(productName).append("</li>");
                html.append("<li><strong>Sản phẩm:</strong> ").append(productName).append(" <br><small><strong>(IMEI/Serial:</strong> ").append(imei).append(")</small></li>");
                html.append("<li><strong>Tình trạng lỗi (Ghi nhận sơ bộ):</strong> ").append(issueDescription).append("</li>");
                html.append("</ul>");
                html.append("<p>Hiện tại, sản phẩm đang ở trạng thái: <strong style='color:#ffc107;'>CHỜ KỸ THUẬT TIẾP NHẬN</strong>.</p>");
                html.append("<p>Hệ thống sẽ tự động gửi email thông báo cho bạn ngay khi có cập nhật mới về tiến độ sửa chữa từ bộ phận Kỹ thuật.</p>");
                html.append("<p>Cảm ơn bạn đã tin tưởng dịch vụ của TechShop!</p>");
                
                message.setContent(html.toString(), "text/html; charset=UTF-8");
                Transport.send(message);
                System.out.println("Email TẠO MỚI bảo hành đã gửi tới: " + toEmail);
                
            } catch (Exception e) {
                System.err.println("Lỗi gửi email tiếp nhận bảo hành: " + e.getMessage());
            }
        }).start();
    }
}