package com.coldbrewcode.contact;

import com.coldbrewcode.aws.ses.SesClientProvider;
import com.coldbrewcode.aws.ses.SesRawEmailSender;
import com.coldbrewcode.contact.service.ContactFromEmailSender;

public class ManualWiring {

    public static final ContactFormRequestHandler INSTANCE = initialize();

    private static ContactFormRequestHandler initialize() {
        final SesClientProvider sesClientProvider = new SesClientProvider();
        final SesRawEmailSender sesRawEmailSender = new SesRawEmailSender(sesClientProvider);
        final ContactFromEmailSender contactFromEmailSender = new ContactFromEmailSender(sesRawEmailSender);

        return new ContactFormRequestHandler(
                contactFromEmailSender
        );
    }
}
