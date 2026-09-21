package org.apache.coyote.http11;

public enum HttpMethod {
    GET, POST;

    public static HttpMethod of(String method) {
        for (HttpMethod httpMethod : HttpMethod.values()) {
            if (method.equals(httpMethod.toString())) {
                return httpMethod;
            }
        }
        throw new IllegalArgumentException("Invalid HTTP method: " + method);
    }
}
