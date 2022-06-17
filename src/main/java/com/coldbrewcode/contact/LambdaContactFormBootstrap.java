package com.coldbrewcode.contact;

import com.coldbrewcode.aws.lambda.api.LambdaRuntimeApi;
import com.coldbrewcode.aws.lambda.config.LambdaConfigRepo;
import com.coldbrewcode.aws.lambda.handler.LambdaApiGatewayRequestProcessor;
import com.coldbrewcode.common.config.ConfigProvider;

public class LambdaContactFormBootstrap {

    private static final ContactFormRequestHandler handler = ManualWiring.INSTANCE;

    public static void main(String[] args) throws Throwable {
        final var configProvider = new ConfigProvider();
        final var lambdaConfigRepo = new LambdaConfigRepo(configProvider);
        final var lambdaRuntimeApi = new LambdaRuntimeApi(lambdaConfigRepo);
        final var lambdaApiGatewayRequestProcessor = new LambdaApiGatewayRequestProcessor(lambdaRuntimeApi);
        while (true) {
            lambdaApiGatewayRequestProcessor.pickUpAndHandleRequest(handler);
        }
    }

}
