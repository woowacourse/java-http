package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;

public enum HttpContentType {
    ALL(new String[]{"*/*"}, "all"),
    HTML(new String[]{"text/html"}, "html"),
    CSS(new String[]{"text/css"}, "css"),
    JS(new String[]{"application/javascript", "text/javascript"}, "js"),
    IMAGE_AVIF(new String[]{"image/avif"}, "avif"),
    ;

    private final List<String> headerLabels;
    private final String extension;

    HttpContentType(String[] headerLabels, String extension) {
        this.headerLabels = Arrays.asList(headerLabels);
        this.extension = extension;
    }

    public static HttpContentType getHttpContentType(String requestContentType) {
        for (HttpContentType httpContentType : HttpContentType.values()) {
            if (httpContentType.headerLabels.contains(requestContentType)) {
                return httpContentType;
            }
        }
        throw new IllegalArgumentException("잘못된 Content Type입니다. : " + requestContentType);
    }

    public static HttpContentType getByFileName(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("잘못된 파일명입니다.: " + fileName);
        }
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
        for (HttpContentType httpContentType : HttpContentType.values()) {
            if (httpContentType.extension.equalsIgnoreCase(ext)) {
                return httpContentType;
            }
        }
        throw new IllegalArgumentException("알 수 없는 파일 확장자입니다: " + ext);
    }

    public String getResponseHeader() {
        return "Content-Type: " + headerLabels.get(0) + ";charset=utf-8";
    }
}
