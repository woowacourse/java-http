package org.apache.coyote.http11;

import java.io.IOException;

public enum ContentType {
    HTML("text/html;charset=utf-8", ".html"),
    CSS("text/css;charset=utf-8", ".css"),
    JAVASCRIPT("application/javascript;charset=utf-8", ".js"),
    PLAIN("text/plain;charset=utf-8", ".txt");

    private final String mimeType;
    private final String suffix;

    ContentType(
            String mimeType,
            String suffix
    ) {
        this.mimeType = mimeType;
        this.suffix = suffix;
    }

    public static ContentType fromPath(String filePath) throws IOException {
        for (ContentType type : values()) {
            if (type.matches(filePath)) {
                return type;
            }
        }
        throw new IOException("지원하지 않는 파일 형식입니다: " + filePath);
    }

    private boolean matches(String filePath) {
        return filePath.endsWith(suffix);
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getSuffix() {
        return suffix;
    }
}

