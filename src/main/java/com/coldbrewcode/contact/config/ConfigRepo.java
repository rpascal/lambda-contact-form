package com.coldbrewcode.contact.config;

import com.coldbrewcode.dataurl.MediaType;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigRepo {

    @Getter
    private String fromEmailAddress = getEnvVar("NO_REPLY_EMAIL_ADDRESS");

    @Getter
    private Optional<Long> maxAttachmentSizeBytes = getEnvVarOption("MAX_ATTACHMENT_SIZE_BYTES").map(Long::valueOf);

    @Getter
    private Optional<Set<MediaType>> validAttachmentMediaTypes =
            getEnvVarOption("VALID_ATTACHMENT_MEDIA_TYPES")
                    .map(x -> x.split(","))
                    .map(x -> Arrays.stream(x).map(MediaType::fromString).collect(Collectors.toUnmodifiableSet()));


    private String getEnvVar(String key) {
        return System.getenv(key);
    }

    private Optional<String> getEnvVarOption(String key) {
        return Optional.ofNullable(System.getenv(key));
    }

}
