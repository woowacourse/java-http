package org.apache.coyote.http11.enums;

public enum ContentType {

    JS(".js","Application/javascript;"),
    CSS(".css","text/css;"),
    HTML(".html","text/html;");

    private final String fileType;
    private final String path;

    ContentType(final String fileType, final String path) {
        this.fileType = fileType;
        this.path = path;
    }

    public static String getContentType(final String path) {
        if (path.endsWith(JS.getFileType())) {
            return JS.getPath();
        }
        if (path.endsWith(CSS.getFileType())) {
            return CSS.getPath();
        }
        return HTML.getPath();
    }

    public String getFileType() {
        return fileType;
    }

    public String getPath() {
        return path;
    }
}
