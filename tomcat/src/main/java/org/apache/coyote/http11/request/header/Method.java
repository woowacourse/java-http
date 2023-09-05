package org.apache.coyote.http11.request.header;

public enum Method {
    GET,
    POST;

    private static final String HTTP_HEADER_DELIMITER = " ";
    private static final int METHOD_INDEX = 0;

    public static Method from(final String header) {
        final String method = header.split(HTTP_HEADER_DELIMITER)[METHOD_INDEX];
        return Method.valueOf(method);
    }
}
