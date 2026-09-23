package org.apache.coyote.http11;

import org.apache.coyote.MimeType;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FormContents {
    private static final String CONTENT_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_VALUE_SIZE = 2;

    private final Map<String, String> contents;

    private FormContents(Map<String, String> contents) {
        this.contents = contents;
    }

    public static FormContents of(String contentType, byte[] body) {
        if (body == null || contentType == null
                || !contentType.startsWith(MimeType.APPLICATION_FORM_URLENCODED.getTypeName())) {
            return empty();
        }
        return parse(new String(body, StandardCharsets.UTF_8));
    }

    public static FormContents empty() {
        return new FormContents(Map.of());
    }

    private static FormContents parse(String encodedContents) {
        Map<String, String> contents = new HashMap<>();

        for (String content : encodedContents.split(CONTENT_DELIMITER)) {
            if (content.isBlank()) {
                continue;
            }
            String[] keyValue = content.split(KEY_VALUE_DELIMITER, KEY_VALUE_SIZE);
            if (keyValue.length != KEY_VALUE_SIZE) {
                continue;
            }
            contents.put(decode(keyValue[0]), decode(keyValue[1]));
        }
        return new FormContents(contents);
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    public Optional<String> find(String key) {
        return Optional.ofNullable(contents.get(key));
    }
}
