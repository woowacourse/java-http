package org.apache.tomcat.util.http.header;

import java.util.Arrays;

import static org.apache.tomcat.util.http.header.HttpContentEncodingType.UTF8;

public enum HttpContentType {

    TEXT_HTML("text/html", UTF8),
    TEXT_PLAIN("text/plain", UTF8),
    APPLICATION_JSON("application/json", UTF8),
    APPLICATION_XML("application/xml", UTF8),
    APPLICATION_JAVASCRIPT("application/javascript", UTF8);
    private final String mimeType;
    private final HttpContentEncodingType encodingType;

    HttpContentType(String mimeType, HttpContentEncodingType encodingType) {
        this.mimeType = mimeType;
        this.encodingType = encodingType;
    }

    public static String encoding(String value) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.mimeType.equals(value))
                .map(contentType -> contentType.mimeType + contentType.encodingType.getEncoding())
                .findFirst().orElse(value);
    }
}
