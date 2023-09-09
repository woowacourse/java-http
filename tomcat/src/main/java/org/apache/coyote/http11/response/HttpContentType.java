package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum HttpContentType {
    HTML("text/html;charset=utf-8", "html"),
    CSS("text/css;charset=utf-8", "css"),
    JAVASCRIPT("text/javascript", "js"),
    PLAIN_TEXT("text/plain;charset=utf-8", "txt");

    private static final Map<String, HttpContentType> CONTENT_TYPE_BY_EXTENSION;

    static {
        CONTENT_TYPE_BY_EXTENSION = Stream.of(HttpContentType.values())
                .collect(Collectors.toMap(
                        httpContentType -> httpContentType.fileExtension,
                        httpContentType -> httpContentType));
    }

    public static HttpContentType getByFilePath(final String filePath) {
        final String[] fileNameSplit = filePath.split("\\.");
        final String fileType = fileNameSplit[fileNameSplit.length - 1];

        return CONTENT_TYPE_BY_EXTENSION.getOrDefault(
                fileType,
                PLAIN_TEXT
        );
    }

    private final String headerString;
    private final String fileExtension;

    HttpContentType(String headerString, String fileExtension) {
        this.headerString = headerString;
        this.fileExtension = fileExtension;
    }

    public String getHeaderString() {
        return headerString;
    }
}
