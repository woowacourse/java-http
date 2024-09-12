package org.apache.tomcat.util.http.header;

import static org.apache.tomcat.util.http.header.HttpContentEncodingType.NONE;
import static org.apache.tomcat.util.http.header.HttpContentEncodingType.UTF8;

import java.util.Arrays;

public enum HttpContentType {

    TEXT_HTML("text/html", UTF8),
    TEXT_PLAIN("text/plain", UTF8),
    APPLICATION_JSON("application/json", UTF8),
    APPLICATION_XML("application/xml", UTF8),
    APPLICATION_JAVASCRIPT("application/javascript", UTF8),
    APPLICATION_OCTET_STREAM("application/octet-stream", NONE),
    APPLICATION_PDF("application/pdf", NONE),
    IMAGE_PNG("image/png", NONE),
    IMAGE_JPEG("image/jpeg", NONE),
    IMAGE_GIF("image/gif", NONE),
    IMAGE_SVG_XML("image/svg+xml", NONE),
    MULTIPART_FORM_DATA("multipart/form-data", NONE),
    APPLICATION_ZIP("application/zip", NONE),
    VIDEO_MP4("video/mp4", NONE),
    AUDIO_MPEG("audio/mpeg", NONE);

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
