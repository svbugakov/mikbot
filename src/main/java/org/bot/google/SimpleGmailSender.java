package org.bot.google;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

public class SimpleGmailSender implements GmailSender {

    private final String fromEmail;
    private final String password;        // Пароль для почтовых программ!
    private final String fromName;   // Добавьте реальное имя

    public SimpleGmailSender(String fromEmail, String password, String fromName) {
        this.fromEmail = fromEmail;
        this.password = password;
        this.fromName = fromName;
    }

    @Override
    public void sendMail(
            String text,
            String toEmail,
            String subject
    ) {

        Properties properties = new Properties();

        // Настройки для порта 465 (SSL) - рекомендуется
        properties.put("mail.smtp.host", "smtp.rambler.ru");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");

        // Современные SSL настройки
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Таймауты
        properties.put("mail.smtp.connectiontimeout", "30000");
        properties.put("mail.smtp.timeout", "30000");
        properties.put("mail.smtp.writetimeout", "30000");

        try {
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            session.setDebug(true); // Включите для диагностики

            Message message = new MimeMessage(session);
            // ВАЖНО: Добавляем имя отправителя
            message.setFrom(new InternetAddress(fromEmail, fromName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            // ВАЖНО: Создаем multipart сообщение вместо простого текста
            MimeMultipart multipart = new MimeMultipart("alternative");

            // Текстовая версия
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(text, "utf-8");

            // HTML версия (можно закомментировать если не нужна)
            MimeBodyPart htmlPart = new MimeBodyPart();
            String htmlContent = "<html><body><p>" + text.replace("\n", "<br>") + "</p></body></html>";
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");

            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            // ВАЖНО: Добавляем заголовки против спама
            message.setHeader("Precedence", "bulk");
            message.setHeader("X-Mailer", "JavaMail");
            message.setSentDate(new Date());

            // ВАЖНО: Устанавливаем нормальный приоритет
            message.setHeader("X-Priority", "3");

            Transport.send(message);
            System.out.println("✅ Письмо успешно отправлено через Rambler!");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

