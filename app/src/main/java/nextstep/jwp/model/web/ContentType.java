package nextstep.jwp.model.web;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html;charset=utf-8", "html"),
    CSS("text/css;charset=utf-8", "css"),
    JS("application/javascript", "js");

    private String contentType;
    private String resourceSuffix;

    ContentType(String contentType, String resourceSuffix) {
        this.contentType = contentType;
        this.resourceSuffix = resourceSuffix;
    }

    public static String contentTypeFromUri(String uri) {
        int delimiterIndex = uri.indexOf(".");
        uri = uri.substring(delimiterIndex);
        return findContentType(uri);
    }

    public static String findContentType(String resourceSuffix) {
        return Arrays.stream(values())
                .filter(value -> value.resourceSuffix.equals(resourceSuffix))
                .map(value -> value.contentType)
                .findAny()
                .orElseThrow(() -> new RuntimeException("content-type not found"));
    }

    public String getContentType() {
        return contentType;
    }
}
