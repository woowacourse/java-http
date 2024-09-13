package org.apache.coyote.http11.request;

public enum HttpMethod {
    GET,
    POST,
    DELETE;

    public static HttpMethod from(String data) {
        if ("GET".equals(data.toUpperCase().strip())) {
            return GET;
        }
        if ("POST".equals(data.toUpperCase().strip())) {
            return POST;
        }
        return DELETE;
    }
}
