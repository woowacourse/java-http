package org.apache.coyote.http11;

import java.util.stream.Stream;

public enum FileExtension {

    HTML("html", "text/html;charset=utf-8"),
    JS("js", "text/javascript"),
    CSS("css", "text/css");

    private final String extension;
    private final String contentType;

    FileExtension(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static String findContentType(String fileName) {
        String fileExtension = fileName.split("\\.")[1];

        return Stream.of(FileExtension.values())
                .filter(f -> f.extension.equals(fileExtension))
                .map(f -> f.contentType)
                .findFirst()
                .orElse("text/html");
    }
}
