package org.apache.coyote.http11.httpmessage.request.requestbody;

import java.util.HashMap;
import java.util.Map;

public class RequestBodyContent {
    private final Map<String, String> body;

    public RequestBodyContent(Map<String, String> body) {
        this.body = body;
    }

    public static RequestBodyContent parse(final String requestBody) {
        final Map<String, String> bodyContents = new HashMap<>();

        for (final String queryString : requestBody.split("&")) {
            final String name = queryString.split("=")[0];
            final String value = queryString.split("=")[1];
            bodyContents.put(name, value);
        }
        return new RequestBodyContent(new HashMap<>(bodyContents));
    }

    public String get(final String key) {
        return body.get(key);
    }
}
