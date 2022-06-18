package com.coldbrewcode.contact.service;

import com.coldbrewcode.aws.ses.SesRawEmailSender;
import com.coldbrewcode.aws.ses.mime.MimeBodyPartCreator;
import com.coldbrewcode.contact.model.ContactFormRequestBody;
import io.vavr.control.Try;
import jakarta.activation.CommandMap;
import jakarta.activation.MailcapCommandMap;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@RequiredArgsConstructor
@Slf4j
public class ContactFromEmailSender {

    static {
        // I think native is causing issues with files are not being copied so properties are not wired
        // up correctly. Doing it manually here.
        // https://stackoverflow.com/questions/21856211/javax-activation-unsupporteddatatypeexception-no-object-dch-for-mime-type-multi/25650033#25650033
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");
    }

    private static final String NO_REPLY_EMAIL_ADDRESS = "no-reply@ryandpascal.com";

    private final SesRawEmailSender sesRawEmailSender;

    public void sendContactForm(ContactFormRequestBody requestBody) throws MessagingException {
        sendEmail(requestBody).getOrElseThrow(e -> {
            log.error("Failed to send contact form email", e);
            return new RuntimeException(e);
        });
    }

    private Try<Void> sendEmail(ContactFormRequestBody requestBody) throws MessagingException {
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        message.setSubject(requestBody.getSubject());
        message.setFrom(NO_REPLY_EMAIL_ADDRESS);

        for (String address : requestBody.getToAddresses()) {
            message.addRecipients(Message.RecipientType.TO, address);
        }

        for (String address : requestBody.getCcAddresses()) {
            message.addRecipients(Message.RecipientType.CC, address);
        }

        for (String address : requestBody.getBccAddresses()) {
            message.addRecipients(Message.RecipientType.BCC, address);
        }


        /*
            Email does not like .js so using .txt
            MimeBodyPart beforeAttachment = MimeBodyPartCreator.attachment(adUnitsBeforePath.toFile(), "before.txt");
            MimeBodyPart afterAttachment = MimeBodyPartCreator.attachment(adUnitsAfterPath.toFile(), "after.txt");
            MimeMultipart msg = new MimeMultipart("mixed");
            msg.addBodyPart(MimeBodyPartCreator.html(bodyHTML));
            msg.addBodyPart(beforeAttachment);
            msg.addBodyPart(afterAttachment);
         */

        MimeMultipart msg = new MimeMultipart("mixed");
        msg.addBodyPart(MimeBodyPartCreator.html(requestBody.getBody()));

        message.setContent(msg);

        return sesRawEmailSender.sendRawEmail(message);
    }


}
