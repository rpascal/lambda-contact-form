package com.coldbrewcode.contact;

import com.coldbrewcode.aws.ses.SesClientProvider;
import com.coldbrewcode.aws.ses.SesRawEmailSender;
import com.coldbrewcode.contact.config.ConfigRepo;
import com.coldbrewcode.contact.service.ContactFromEmailSender;

public class ManualWiring {

    public static final ContactFormRequestHandler INSTANCE = initialize();

    private static ContactFormRequestHandler initialize() {
        final SesClientProvider sesClientProvider = new SesClientProvider();
        final SesRawEmailSender sesRawEmailSender = new SesRawEmailSender(sesClientProvider);
        final ConfigRepo configRepo = new ConfigRepo();
        final ContactFromEmailSender contactFromEmailSender = new ContactFromEmailSender(sesRawEmailSender, configRepo);

        return new ContactFormRequestHandler(
                contactFromEmailSender
        );
    }
}
