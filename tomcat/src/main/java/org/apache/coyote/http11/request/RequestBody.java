package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final String rawBody;
    private final Map<String, String> parameters;

    public RequestBody(final String rawBody) {
        this.rawBody = rawBody;
        this.parameters = parseParameters(rawBody);
    }

    private Map<String, String> parseParameters(
            final String requestBody
    ) {
        final Map<String, String> parameters = new HashMap<>();

        if (requestBody == null || requestBody.isBlank()) {
            return parameters;
        }

        for (String parameter : requestBody.split("&")) {
            final String[] keyValue = parameter.split("=", 2);

            if (keyValue.length != 2) {
                continue;
            }

            final String key = decode(keyValue[0]);
            final String value = decode(keyValue[1]);

            parameters.put(key, value);
        }

        return parameters;
    }

    private String decode(final String value) {
        return URLDecoder.decode(
                value,
                StandardCharsets.UTF_8
        );
    }

    public String getRawBody() {
        return rawBody;
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }
}