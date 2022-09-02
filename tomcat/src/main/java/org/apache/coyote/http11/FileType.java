package org.apache.coyote.http11;

import java.util.Arrays;

public enum FileType {
    HTML("html", "html"),
    CSS("css", "css"),
    JAVASCRIPT("js", "javascript");

    private String endUrl;
    private String type;

    FileType(String endUrl, String type) {
        this.endUrl = endUrl;
        this.type = type;
    }

    public static FileType from(String url) {
        String endUrl = url.split("\\.")[1];
        return Arrays.stream(FileType.values())
                .filter(fileType -> fileType.getEndUrl().equals(endUrl))
                .findFirst()
                .orElseThrow();
    }

    public String getEndUrl() {
        return endUrl;
    }

    public String getType() {
        return type;
    }
}
