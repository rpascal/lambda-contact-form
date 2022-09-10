package com.coldbrewcode.contact;

import com.coldbrewcode.aws.lambda.api.exceptions.NoHandlerFoundException;
import com.coldbrewcode.aws.lambda.handler.LambdaApiGatewayRequestHandler;
import com.coldbrewcode.aws.lambda.http.HttpMethod;
import com.coldbrewcode.aws.lambda.model.ApiErrorResponseBodyWrapper;
import com.coldbrewcode.aws.lambda.model.ApiRequest;
import com.coldbrewcode.aws.lambda.model.ApiResponse;
import com.coldbrewcode.common.jackson.Mapper;
import com.coldbrewcode.contact.model.ContactFormRequestBody;
import com.coldbrewcode.contact.service.ContactFromEmailSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static io.vavr.API.*;
import static io.vavr.Patterns.$Tuple2;

@Slf4j
@RequiredArgsConstructor
public class ContactFormRequestHandler implements LambdaApiGatewayRequestHandler {

    private final ContactFromEmailSender contactFromEmailSender;

    @Override
    public ApiResponse handle(ApiRequest input) {
        return Try.of(() ->
                Match(input.getHttpMethod())
                        .of(
                                Case($(HttpMethod.OPTIONS), this::cors),
                                Case($(), () -> handleNonCorsRequest(input))
                        )
        ).getOrElseGet(t -> mapThrowableToApiResponse(t, input));
    }


    private ApiResponse mapThrowableToApiResponse(Throwable throwable, ApiRequest request) {
        tryLogRequest(request);
        if (throwable instanceof NoHandlerFoundException) {
            return ApiResponse.notFound(ApiErrorResponseBodyWrapper.create("No handler found", throwable));
        } else {
            log.error("Unmapped exception thrown", throwable);
            return ApiResponse.internalServer(ApiErrorResponseBodyWrapper.create(throwable));
        }
    }

    private void tryLogRequest(ApiRequest request) {
        try {
            final ObjectMapper mapper = Mapper.MAPPER;
            final val requestPrettyString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(request.body()));
            final val requestString = request.body();
            log.info("FailureRequestInfo: method:" + request.getHttpMethod().name() + " path:" + request.path());
            log.info("FailureRequestInfo: pretty body:" + requestString);
            log.info("FailureRequestInfo: raw body:" + requestPrettyString);
        } catch (JsonProcessingException e) {
            log.error("Failed to log request information", e);
        }
    }

    private ApiResponse handleNonCorsRequest(final ApiRequest request) {
        log.info("Handling request: " + request.path());
        return Match(request.route()).of(
                Case($Tuple2($(HttpMethod.POST), $("/api/contact")),  () -> sendContactFormEmail(request.getBodyAs(ContactFormRequestBody.class)).get()),
                Case($Tuple2($(HttpMethod.POST), $("/contact")),  () -> sendContactFormEmail(request.getBodyAs(ContactFormRequestBody.class)).get()),
                Case($(), () -> {
                    throw NoHandlerFoundException.create(request);
                })
        );
    }

    private Try<ApiResponse> sendContactFormEmail(ContactFormRequestBody contactFormRequestBody) {
        return contactFromEmailSender
                .sendContactForm(contactFormRequestBody)
                .map(v -> ApiResponse.ok());
    }

    private ApiResponse cors() {
        log.info("Handling cors request");
        return ApiResponse.ok();
    }

}
