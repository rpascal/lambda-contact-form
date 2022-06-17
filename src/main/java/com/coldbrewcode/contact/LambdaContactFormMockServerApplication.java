package com.coldbrewcode.contact;

import com.coldbrewcode.aws.lambda.mock.MockLambdaApiRequestProcessor;

public class LambdaContactFormMockServerApplication {

    private static final ContactFormRequestHandler handler = ManualWiring.INSTANCE;

    public static void main(String[] args) {
        new MockLambdaApiRequestProcessor().startServer(4578, handler);
    }


}
