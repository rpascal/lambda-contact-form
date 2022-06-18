package com.coldbrewcode.aws.lambda.api.exceptions;

import com.coldbrewcode.aws.lambda.model.ApiRequest;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class NoHandlerFoundException extends RuntimeException {

    private final String httpMethod;
    private final String requestUrl;

    private NoHandlerFoundException(String httpMethod, String requestUrl) {
        super("No handler could be found for " + httpMethod + ": " + requestUrl);
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
    }

    public static NoHandlerFoundException create(ApiRequest apiRequest) {
        return new NoHandlerFoundException(
                apiRequest.getHttpMethod().name(),
                apiRequest.path()
        );
    }
}
