package org.apache.coyote.util;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record ResourceDecodedUrl(
        String value
) {

    private static final Logger log = LoggerFactory.getLogger(ResourceDecodedUrl.class);

    public static ResourceDecodedUrl from(final String resourcePath) {
        URL resourceUrl = ResourceDecodedUrl.class.getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            log.info("요청할 수 없는 자원 경로 = {}", resourcePath);
            throw new IllegalArgumentException("요청한 자원을 찾을 수 없습니다.");
        }
        String decodedUrl = URLDecoder.decode(resourceUrl.getPath(), StandardCharsets.UTF_8);

        return new ResourceDecodedUrl(decodedUrl);
    }
}
