package nextstep;

import java.util.Arrays;

public enum RequestFile {

    INDEX_HTML("/index.html", "/index.html", Constants.TEXT_HTML_CHARSET_UTF_8),
    CSS("/css/styles.css", "/css/styles.css", Constants.TEXT_CSS_CHARSET_UTF_8),
    // TODO: account & password 변수로 바꾸기
    LOGIN("/login?account=gugu&password=password", "/login.html", Constants.TEXT_HTML_CHARSET_UTF_8),
    ;

    private final String requestPath;
    private final String fileName;
    private final String contentType;

    RequestFile(final String requestPath, final String fileName, final String contentType) {
        this.requestPath = requestPath;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public String getFilePath() {
        return "static" + this.fileName;
    }

    public static RequestFile getFile(final String filePath) {
        return Arrays.stream(RequestFile.values())
                     .filter(fileName -> fileName.requestPath.equals(filePath))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("잘못된 파일 경로입니다. " + filePath));
    }

    public String getContentType() {
        return this.contentType;
    }

    private static class Constants {

        private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";
        private static final String TEXT_CSS_CHARSET_UTF_8 = "text/css;charset=utf-8";
    }
}
