package nextstep;

import java.util.Arrays;

public enum RequestFile {

    INDEX_HTML("/index.html", "index.html", Constants.TEXT_HTML_CHARSET_UTF_8, Constants.RESPONSE_HEADER, false),
    CSS("/css/styles.css", "css/styles.css", Constants.TEXT_CSS_CHARSET_UTF_8, Constants.RESPONSE_HEADER, false),
    LOGIN_API("/login", "login.html", Constants.TEXT_HTML_CHARSET_UTF_8, Constants.RESPONSE_HEADER, false),
    HTML_401("/401.html", "/401.html", Constants.TEXT_HTML_CHARSET_UTF_8, Constants.RESPONSE_HEADER, false),
    ;

    private final String requestPath;
    private final String fileName;
    private final String contentType;
    private final String responseHeader;
    private final boolean isApi;

    RequestFile(final String requestPath, final String fileName, final String contentType, final String responseHeader, final boolean isApi) {
        this.requestPath = requestPath;
        this.fileName = fileName;
        this.contentType = contentType;
        this.responseHeader = responseHeader;
        this.isApi = isApi;
    }

    public String getFilePath() {
        return "static/" + this.fileName;
    }

    public static RequestFile getFile(String filePath) {
        if (filePath.contains(Constants.QUESTION_MARK)) {
            final int index = filePath.indexOf(Constants.QUESTION_MARK);
            filePath = filePath.substring(0, index);
        }
        final String finalFilePath = filePath;
        return Arrays.stream(RequestFile.values())
                     .filter(fileName -> fileName.requestPath.equals(finalFilePath))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("잘못된 파일 경로입니다. " + finalFilePath));
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getResponseHeader() {
        return this.responseHeader;
    }

    public boolean isApi() {
        return this.isApi;
    }

    private static class Constants {

        private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";
        private static final String TEXT_CSS_CHARSET_UTF_8 = "text/css;charset=utf-8";
        private static final String QUESTION_MARK = "?";
        private static final String RESPONSE_HEADER = "HTTP/1.1 200 OK ";
    }
}
