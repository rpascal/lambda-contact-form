package com.coldbrewcode.contact.service;

import com.coldbrewcode.aws.ses.SesRawEmailSender;
import com.coldbrewcode.aws.ses.mime.MimeBodyPartCreator;
import com.coldbrewcode.contact.config.ConfigRepo;
import com.coldbrewcode.contact.model.ContactFormAttachment;
import com.coldbrewcode.contact.model.ContactFormRequestBody;
import com.coldbrewcode.dataurl.Base64DataUrl;
import io.vavr.control.Try;
import jakarta.activation.CommandMap;
import jakarta.activation.MailcapCommandMap;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
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

    private final SesRawEmailSender sesRawEmailSender;
    private final ConfigRepo configRepo;

    public Try<Void> sendContactForm(ContactFormRequestBody requestBody) {
        return buildMessage(requestBody)
                .flatMap(mimeMessage -> sesRawEmailSender.sendRawEmail(mimeMessage));
    }

    private Try<MimeMessage> buildMessage(ContactFormRequestBody requestBody) {
        return Try.of(() -> {
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session);

            message.setSubject(requestBody.getSubject());
            message.setFrom(configRepo.getFromEmailAddress());

            if (!requestBody.getReplyToAddresses().isEmpty()) {
                final InternetAddress[] replyToArray = new InternetAddress[requestBody.getReplyToAddresses().size()];
                for (String address : requestBody.getReplyToAddresses()) {
                    replyToArray[0] = InternetAddress.parse(address)[0];
                }
                message.setReplyTo(replyToArray);
            }

            for (String address : requestBody.getToAddresses()) {
                message.addRecipients(Message.RecipientType.TO, address);
            }

            for (String address : requestBody.getCcAddresses()) {
                message.addRecipients(Message.RecipientType.CC, address);
            }

            for (String address : requestBody.getBccAddresses()) {
                message.addRecipients(Message.RecipientType.BCC, address);
            }

            MimeMultipart msg = new MimeMultipart("mixed");

            for (ContactFormAttachment attachment : requestBody.getAttachments()) {
                final File attachmentFile = readAndValidateAttachment(attachment);
                final MimeBodyPart bodyPartAttachment = MimeBodyPartCreator.attachment(attachmentFile, attachment.getFileNameWithAttachment());
                msg.addBodyPart(bodyPartAttachment);
            }

            msg.addBodyPart(
                    MimeBodyPartCreator.html(
                            requestBody
                                    .getBody()
                                    .replace("\n", "<br />")
                    )
            );

            message.setContent(msg);
            return message;
        });
    }

    private File readAndValidateAttachment(ContactFormAttachment attachment) throws IOException {
        final var data = Base64DataUrl.fromUrl(attachment.getBase64DataUrl());

        final File tempFile = File.createTempFile(
                "ContactFromEmailSender",
                attachment.getFileNameWithAttachment(),
                null
        );
        final FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(data.getData());
        fos.close();

        configRepo.getMaxAttachmentSizeBytes().ifPresent(maxAttachmentSize -> {
            if (tempFile.length() > maxAttachmentSize) {
                throw new IllegalArgumentException("Invalid attachment, too big.");
            }
        });

        configRepo.getValidAttachmentMediaTypes().ifPresent(validMediaTypes -> {
            if (!validMediaTypes.contains(data.getMediaType())) {
                throw new IllegalArgumentException("Invalid attachment, unsupported media type: " + data.getMediaType());
            }
        });
        return tempFile;
    }

}
