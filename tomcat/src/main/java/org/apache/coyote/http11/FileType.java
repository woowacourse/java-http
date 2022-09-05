package org.apache.coyote.http11;

import java.util.Arrays;

public enum FileType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JAVASCRIPT("js", "application/javascript");

    private final String extension;
    private final String mimeType;

    FileType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static FileType from(String url) {
        String extension = url.split("\\.")[1];
        return Arrays.stream(FileType.values())
                .filter(fileType -> fileType.getExtension().equals(extension))
                .findFirst()
                .orElseThrow();
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }
}
