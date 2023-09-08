package org.apache.coyote.http11.request.body;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class RequestBody {

    private static final String BODY_STRING_SEPARATOR = "&";
    private static final String BODY_VALUE_SEPARATOR = "=";
    private static final int BODY_NAME_INDEX = 0;
    private static final int BODY_VALUE_INDEX = 1;

    private final Map<String, String> requestBody;

    private RequestBody(final Map<String, String> requestBody) {
        this.requestBody = requestBody;
    }

    public static RequestBody from(final String requestBody) {
        if (requestBody.isBlank()) {
            return new RequestBody(new HashMap<>());
        }

        return parseRequestBody(requestBody);
    }

    private static RequestBody parseRequestBody(final String requestBody) {
        final Map<String, String> body = new HashMap<>();

        final StringTokenizer stringTokenizer = new StringTokenizer(requestBody, BODY_STRING_SEPARATOR);
        while(stringTokenizer.hasMoreTokens()) {
            final String bodyParameter = stringTokenizer.nextToken();
            final String[] split = bodyParameter.split(BODY_VALUE_SEPARATOR);
            body.put(split[BODY_NAME_INDEX], split[BODY_VALUE_INDEX]);
        }

        return new RequestBody(body);
    }


    public String search(final String key) {
        if (!requestBody.containsKey(key)) {
            return null;
        }

        return requestBody.get(key);
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RequestBody that = (RequestBody) o;
        return Objects.equals(requestBody, that.requestBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestBody);
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "requestBody=" + requestBody +
                '}';
    }
}
