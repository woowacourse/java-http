package kokodak.http;

import java.util.Arrays;

public enum ContentType {

    TEXT_HTML("text/html", "html", "text/html;charset=utf-8"),
    TEXT_CSS("text/css", "css", "text/css;charset=utf-8"),
    APPLICATION_JAVASCRIPT("application/javascript", "js", "application/javascript"),
    ;

    private final String acceptHeader;
    private final String fileNameExtension;
    private final String contentType;

    ContentType(final String acceptHeader,
                final String fileNameExtension,
                final String contentType) {
        this.acceptHeader = acceptHeader;
        this.fileNameExtension = fileNameExtension;
        this.contentType = contentType;
    }

    public static String from(final String fileName, final String acceptHeader) {
        final String fileNameExtension = fileName.substring(fileName.indexOf(".") + 1);
        return Arrays.stream(values())
                     .filter(contentType -> matchType(contentType, acceptHeader, fileNameExtension))
                     .map(contentType -> contentType.contentType)
                     .findAny()
                     .orElseThrow(() -> new IllegalArgumentException("Invalid ContentType"));
    }

    private static boolean matchType(final ContentType contentType,
                                     final String acceptHeader,
                                     final String fileNameExtension) {
        return contentType.acceptHeader.contains(acceptHeader) || contentType.fileNameExtension.equals(fileNameExtension);
    }

    public String getAcceptHeader() {
        return acceptHeader;
    }

    public String getFileNameExtension() {
        return fileNameExtension;
    }

    public String getContentType() {
        return contentType;
    }
}
