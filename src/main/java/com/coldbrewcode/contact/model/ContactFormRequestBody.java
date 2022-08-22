package com.coldbrewcode.contact.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class ContactFormRequestBody {

    @JsonProperty("replyToAddresses")
    private final List<String> replyToAddresses;

    @JsonProperty("toAddresses")
    private final List<String> toAddresses;

    @JsonProperty("ccAddresses")
    private final List<String> ccAddresses;

    @JsonProperty("bccAddresses")
    private final List<String> bccAddresses;

    @JsonProperty("subject")
    private final String subject;

    @JsonProperty("body")
    private final String body;

}
