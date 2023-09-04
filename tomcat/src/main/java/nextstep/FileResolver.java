package nextstep;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;

public enum FileResolver {

    INDEX_HTML("/index.html", "index.html", Constants.TEXT_HTML_CHARSET_UTF_8, Constants.RESPONSE_HEADER),
    CSS("/css/styles.css", "css/styles.css", Constants.TEXT_CSS_CHARSET_UTF_8, Constants.RESPONSE_HEADER),
    LOGIN("/login", "login.html", Constants.TEXT_HTML_CHARSET_UTF_8, Constants.RESPONSE_HEADER),
    REGISTER("/register", "register.html", Constants.TEXT_HTML_CHARSET_UTF_8, Constants.RESPONSE_HEADER),
    HTML_401("/401.html", "/401.html", Constants.TEXT_HTML_CHARSET_UTF_8, Constants.RESPONSE_HEADER),
    ;

    private final String requestPath;
    private final String fileName;
    private final String contentType;
    private final String responseHeader;

    FileResolver(final String requestPath, final String fileName, final String contentType, final String responseHeader) {
        this.requestPath = requestPath;
        this.fileName = fileName;
        this.contentType = contentType;
        this.responseHeader = responseHeader;
    }

    public static boolean containsExtension(final String parsedUri) {
        return parsedUri.contains(Constants.CSS_EXTENSION)
                || parsedUri.contains(Constants.HTML_EXTENSION)
                || parsedUri.contains(Constants.JS_EXTENSION);
    }

    public static FileResolver findFile(final String parsedUri) {
        return Arrays.stream(FileResolver.values())
                     .filter(file -> file.requestPath.equals(parsedUri))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("잘못된 파일명이 매핑되었습니다."));
    }

    public String getFilePath() {
        return "static/" + this.fileName;
    }

    public static FileResolver getFile(String filePath) {
        if (filePath.contains(Constants.QUESTION_MARK)) {
            final int index = filePath.indexOf(Constants.QUESTION_MARK);
            filePath = filePath.substring(0, index);
        }
        final String finalFilePath = filePath;
        return Arrays.stream(FileResolver.values())
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

    public String getResponse() throws IOException {
        final URL url = getClass().getClassLoader().getResource(this.getFilePath());
        final var responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));
        return String.join("\r\n",
                this.getResponseHeader(),
                "Content-Type: " + this.getContentType(),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private static class Constants {

        public static final String RESPONSE_HEADER = "HTTP/1.1 200 OK ";
        private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";
        private static final String TEXT_CSS_CHARSET_UTF_8 = "text/css;charset=utf-8";
        private static final String QUESTION_MARK = "?";
        private static final String CSS_EXTENSION = ".css";
        private static final String HTML_EXTENSION = ".html";
        private static final String JS_EXTENSION = ".js";
    }
}
