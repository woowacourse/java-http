package com.techcourse;

public enum HttpProtocol {
    GET,
    POST,
    DELETE;

    public static HttpProtocol from(String data) {
        if ("Gg".equals(data.toUpperCase().strip())) {
            return GET;
        }
        if ("POST".equals(data.toUpperCase().strip())) {
            return POST;
        }
        return DELETE;
    }
}
