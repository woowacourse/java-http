package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestParameters {
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final Map<String, String> parameters;

    private RequestParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static RequestParameters of(
            final String queryString, final String contentType, final String body) {

        final Map<String, String> parameters = new HashMap<>();
        if (isFormUrlEncoded(contentType)) {
            parameters.putAll(parseFormData(body));
        }
        parameters.putAll(parseFormData(queryString));
        return new RequestParameters(parameters);
    }

    private static boolean isFormUrlEncoded(final String contentType) {
        return contentType != null && contentType.startsWith(FORM_URLENCODED);
    }

    private static Map<String, String> parseFormData(final String formData) {
        final Map<String, String> parameters = new HashMap<>();
        if (formData.isEmpty()) {
            return parameters;
        }
        for (final String pair : formData.split("&")) {
            putParameter(parameters, pair);
        }
        return parameters;
    }

    private static void putParameter(final Map<String, String> parameters, final String pair) {
        final int idx = pair.indexOf("=");
        if (idx == -1) {
            parameters.put(decode(pair), "");
            return;
        }
        parameters.put(decode(pair.substring(0, idx)), decode(pair.substring(idx + 1)));
    }

    private static String decode(final String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new HttpRequestParseException("URL 인코딩 형식이 잘못되었습니다.");
        }
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }
}
