package org.apache.coyote.http11;

public class Http11ContentTypeParser {

    public static String parse(String path) {
        if (path.endsWith(".html")) {
            return "text/html";
        }
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }
        if (path.endsWith(".ico")) {
            return "image/x-icon";
        }
        throw new IllegalArgumentException("ContentType을 지원하지 않는 경로입니다: " + path);
    }
}
