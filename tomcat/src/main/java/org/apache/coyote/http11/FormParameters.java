package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

class FormParameters {

    private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";

    private final Map<String, String> values;

    FormParameters(String contentType, byte[] body) {
        if (!isFormUrlEncoded(contentType) || body.length == 0) {
            this.values = Map.of();
            return;
        }
        this.values = Map.copyOf(parse(body));
    }

    private boolean isFormUrlEncoded(String contentType) {
        if (contentType == null) {
            return false;
        }

        String mediaType = extractMediaType(contentType);
        return FORM_URL_ENCODED.equalsIgnoreCase(mediaType);
    }

    private String extractMediaType(String contentType) {
        int optionSeparatorIndex = contentType.indexOf(';');
        if (optionSeparatorIndex < 0) {
            return contentType.trim();
        }
        return contentType.substring(0, optionSeparatorIndex).trim();
    }

    private Map<String, String> parse(byte[] body) {
        Map<String, String> parameters = new LinkedHashMap<>();
        String encodedBody = new String(body, StandardCharsets.UTF_8);
        Arrays.stream(encodedBody.split("&"))
                .forEach(field -> putDecodedParameter(parameters, field));
        return parameters;
    }

    private void putDecodedParameter(Map<String, String> parameters, String field) {
        int separatorIndex = field.indexOf('=');
        if (separatorIndex < 0) {
            parameters.put(decode(field), "");
            return;
        }

        String name = field.substring(0, separatorIndex);
        String value = field.substring(separatorIndex + 1);
        parameters.put(decode(name), decode(value));
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    String get(String name) {
        return values.get(name);
    }
}
