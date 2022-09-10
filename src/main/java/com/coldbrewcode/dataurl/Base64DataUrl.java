package com.coldbrewcode.dataurl;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

@RequiredArgsConstructor
@Slf4j
public class Base64DataUrl {

    @Getter
    private final byte[] data;
    @Getter
    private final MediaType mediaType;

    public static Base64DataUrl fromUrl(String base64DataUrlString) {
        final String[] split = base64DataUrlString.split(",");
        if (split.length != 2) {
            log.error("Unable to parse data url" + base64DataUrlString);
            throw new IllegalArgumentException("Unable to parse data url = " + base64DataUrlString);
        }
        final var dataString = split[1];
        final var prefix = split[0];

        if (!prefix.startsWith("data:")) {
            log.error("Data url does not start with 'data:', " + dataString);
            throw new IllegalArgumentException("Data url does not start with 'data:', " + dataString);
        }

        final var prefixNoData = prefix.substring(5);
        final var mediaTypeString = prefixNoData.split(";")[0];

        final var mediaType = MediaType.fromString(mediaTypeString);
        final var bytes = Base64.getDecoder().decode(dataString);

        return new Base64DataUrl(bytes, mediaType);
    }

}
