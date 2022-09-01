package org.apache.coyote;

import java.util.Arrays;

public enum FilePath {

    DEFAULT("/", null),
    INDEX("/index.html", "index.html"),
    NOT_FOUND("", "404.html");

    private String url;
    private String filePath;

    FilePath(final String url, final String fileName) {
        this.url = url;
        this.filePath = fileName;
    }

    public static FilePath findFileName(final String requestUrl){
        return Arrays.stream(FilePath.values())
                .filter(it -> it.url.equals(requestUrl))
                .findFirst()
                .orElse(NOT_FOUND);
    }

    public String getFilePath() {
        return filePath;
    }

}
