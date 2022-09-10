package com.coldbrewcode.contact.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class ContactFormAttachment {

    @JsonProperty("fileNameWithAttachment")
    private final String fileNameWithAttachment;
    @JsonProperty("base64DataUrl")
    private final String base64DataUrl;

}
