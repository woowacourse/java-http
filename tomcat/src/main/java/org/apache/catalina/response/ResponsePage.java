package org.apache.catalina.response;

import java.util.Optional;

public enum ResponsePage {
    LOGIN("/login", HttpStatus.OK, "/login.html"),
    ;

    private final String url;
    private final HttpStatus status;
    private final String fileName;

    ResponsePage(String url, HttpStatus status, String fileName) {
        this.url = url;
        this.status = status;
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getFileName() {
        return fileName;
    }

    public static Optional<ResponsePage> fromUrl(String url) {
        for (ResponsePage page : values()) {
            if (page.url.equals(url)) {
                return Optional.of(page);
            }
        }
        return Optional.empty();
    }
}
