package org.apache.coyote.http11;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    OPTION,
    ;

    public static HttpMethod from(final String methodName) {
        final String upperCaseName = methodName.toUpperCase();
        return valueOf(upperCaseName);
    }
}
