package org.bot.google;

public interface GmailSender {
    void sendMail(
            String text,
            String toEmail,
            String subject
    );
}
