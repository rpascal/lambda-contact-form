package com.coldbrewcode.contact;

public class ManualWiring {

    public static final ContactFormRequestHandler INSTANCE = initialize();

    private static ContactFormRequestHandler initialize() {
        return new ContactFormRequestHandler(

        );
    }
}
