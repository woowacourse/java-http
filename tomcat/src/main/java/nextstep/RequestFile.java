package nextstep;

import java.util.Arrays;

public enum RequestFile {

    INDEX_HTML("/index.html", "text/html;charset=utf-8"),
    CSS("/css/styles.css", "text/css;charset=utf-8"),
    ;

    private final String requestPath;
    private final String contentType;

    RequestFile(final String requestPath, final String contentType) {
        this.requestPath = requestPath;
        this.contentType = contentType;
    }

    public String getFilePath() {
        return "static/" + this.requestPath;
    }

    public static RequestFile getFile(final String filePath) {
        return Arrays.stream(RequestFile.values())
                     .filter(fileName -> fileName.requestPath.equals(filePath))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("잘못된 파일 경로입니다."));
    }

    public String getContentType() {
        return this.contentType;
    }
}
