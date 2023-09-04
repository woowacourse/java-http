package org.apache.coyote.http11;

public enum HttpMethod {
    POST,
    GET,
    PUT,
    DELETE;

    public static HttpMethod of(String httpMethod) {
        return HttpMethod.valueOf(httpMethod.toUpperCase());
    }

    public boolean isGet() {
        return this.equals(GET);
    }
}
