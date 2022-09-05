package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestBody {

    private static final String BODY_AND = "&";
    private static final String BODY_EQUAL = "=";

    private final Map<String, String> body;

    public HttpRequestBody(final String body) {
        this.body = parseBody(body);
    }

    private Map<String, String> parseBody(final String requestBody) {
        final Map<String, String> body = new LinkedHashMap<>();
        if (requestBody.isBlank()) {
            return body;
        }
        final String[] requestBodies = requestBody.split(BODY_AND);
        for (String request : requestBodies) {
            final String[] parameterAndValue = request.split(BODY_EQUAL);
            body.put(parameterAndValue[0], parameterAndValue[1]);
        }
        return body;
    }

    public String getBodyValue(final String parameter) {
        if (body.isEmpty() || !body.containsKey(parameter)) {
            return "";
        }
        return body.get(parameter);
    }
}
