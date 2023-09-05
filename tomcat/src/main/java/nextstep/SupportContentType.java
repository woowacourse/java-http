package nextstep;

public enum SupportContentType {
    TEXT_HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css"),
    SCRIPT(".js", "text/javascript"),
    ICON(".ico", "image/x-icon"),
    SVG(".svg", "image/svg+xml");

    private final String endWith;
    private final String contentType;

    SupportContentType(final String endWith, final String contentType) {
        this.endWith = endWith;
        this.contentType = contentType;
    }

    public static String getContentType(String fileName) {
        for (final SupportContentType value : values()) {
            if (fileName.endsWith(value.endWith)) {
                return value.contentType;
            }
        }
        return TEXT_HTML.contentType;
    }
}
