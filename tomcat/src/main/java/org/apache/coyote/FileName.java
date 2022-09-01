package org.apache.coyote;

import java.util.Arrays;

public enum FileName {

    DEFAULT("/", ""),
    LOGIN("/login","login.html"),
    NOT_FOUND("", "404.html");

    private String url;
    private String fileName;

    FileName(final String url, final String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    public static FileName findFileName(final String requestUrl){
        return Arrays.stream(FileName.values())
                .filter(it -> it.url.equals(requestUrl))
                .findFirst()
                .orElse(NOT_FOUND);
    }

    public String getFileName() {
        return fileName;
    }

}
