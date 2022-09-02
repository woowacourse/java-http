package org.apache.http;

public enum HttpMethod {

    GET, POST, PATCH, PUT, DELETE;

    public static boolean isStartWithAny(final String value) {
        for (HttpMethod httpMethod : values()) {
            if (value.startsWith(httpMethod.name())) {
                return true;
            }
        }
        return false;
    }
}
