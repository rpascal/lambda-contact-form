package com.coldbrewcode.dataurl;

import lombok.Getter;

public enum MediaType {

    IMAGE_JPEG("image/jpeg", "jpeg"),
    IMAGE_PNG("image/png", "png");

    @Getter
    private final String mediaTypeString;
    @Getter
    private final String fileExtension;

    MediaType(String type, String fileExtension) {
        this.mediaTypeString = type;
        this.fileExtension = fileExtension;
    }

    public static MediaType fromString(String text) {
        for (MediaType b : MediaType.values()) {
            if (b.mediaTypeString.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unknown media type: " + text);
    }
}
