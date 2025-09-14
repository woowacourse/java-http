package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBodyParams {

    private final Map<String, String> requestBodyParam = new HashMap<>();

    public void parseRequestBodyParams(final String body) {
        if (body == null || body.isEmpty()) {
            return;
        }
        final String[] paramPairs = body.split("&");
        for (String paramPair : paramPairs) {
            final String[] parts = paramPair.split("=", 2);
            if (parts.length == 2) {
                final String paramName = parts[0].trim();
                final String paramValue = parts[1].trim();
                addBodyParam(paramName, paramValue);
            }
        }
    }

    public void addBodyParam(final String paramName, final String paramValue) {
        requestBodyParam.put(paramName, paramValue);
    }

    public String getBodyParam(final String paramName) {
        return requestBodyParam.get(paramName);
    }
}
