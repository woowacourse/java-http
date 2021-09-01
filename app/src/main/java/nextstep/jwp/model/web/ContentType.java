package nextstep.jwp.model.web;

public enum ContentType {
    HTML("text/html;charset=utf-8", "html"),
    CSS("text/css;charset=utf-8", "css"),
    JS("text/js;charset=utf-8", "js");

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
        for (ContentType type : values()) {
            if (type.resourceSuffix.equals(resourceSuffix)) {
                return type.contentType;
            }
        }
        throw new RuntimeException("content-type not found");
    }

    public String getContentType() {
        return contentType;
    }
}
