package com.coldbrewcode.contact;

import com.coldbrewcode.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.coldbrewcode.aws.lambda.handler.LambdaApiGatewayRequestHandler;
import com.coldbrewcode.aws.lambda.http.HttpMethod;
import com.coldbrewcode.aws.lambda.model.ApiErrorResponseBodyWrapper;
import com.coldbrewcode.aws.lambda.model.ApiRequest;
import com.coldbrewcode.aws.lambda.model.ApiResponse;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.vavr.API.*;

@Slf4j
@RequiredArgsConstructor
public class ContactFormRequestHandler implements LambdaApiGatewayRequestHandler {

    @Override
    public ApiResponse handle(ApiRequest input) {
        return Try.of(() ->
                Match(input.getHttpMethod())
                        .of(
                                Case($(HttpMethod.OPTIONS), this::cors),
                                Case($(), () -> handleNonCorsRequest(input))
                        )
        ).getOrElseGet(this::mapThrowableToApiResponse);
    }


    private ApiResponse mapThrowableToApiResponse(Throwable throwable) {
        log.error("Unmapped exception thrown", throwable);
        return ApiResponse.internalServer(ApiErrorResponseBodyWrapper.create(throwable));
    }

    private ApiResponse handleNonCorsRequest(final ApiRequest input) {
        log.info("Handling normal request");
        return ApiResponse.ok(input.path());
    }


    private ApiResponse cors() {
        log.info("Handling cors request");
        return ApiResponse.ok();
    }


}
