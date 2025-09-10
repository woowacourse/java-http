package org.apache.coyote.http11.message.response;

import java.util.Arrays;

public enum ContentType {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "application/javascript"),
    PNG(".png", "image/png"),
    JPG(".jpg", "image/jpeg"),
    JPEG(".jpeg", "image/jpeg"),
    GIF(".gif", "image/gif"),
    PLAIN(".txt", "text/plain"),
    FORM_URLENCODED(null, "application/x-www-form-urlencoded"),
    DEFAULT(null, "application/octet-stream");

    public static final String UTF_8 = ";charset=utf-8";
    private final String extension;
    private final String mimeType;

    ContentType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        if (mimeType.startsWith("text/") || mimeType.equals("application/javascript") || mimeType.equals(
                "application/x-www-form-urlencoded")) {
            return mimeType + UTF_8;
        }
        return mimeType;
    }

    //TODO: 널/대소문자/쿼리스트링 케이스 처리하기  (2025-09-7, 일, 17:24)
    // https://github.com/woowacourse/java-http/pull/800#discussion_r2326895296
    public static ContentType fromPath(String path) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.extension != null && path.endsWith(contentType.extension))
                .findFirst()
                .orElse(DEFAULT);
    }

    public static ContentType fromMimeType(String rawMimeType) {
        if (rawMimeType == null) {
            return DEFAULT;
        }

        String mimeType = rawMimeType.split(";")[0];
        return Arrays.stream(values())
                .filter(contentType -> contentType.mimeType.equalsIgnoreCase(mimeType))
                .findFirst()
                .orElse(DEFAULT);
    }
}
