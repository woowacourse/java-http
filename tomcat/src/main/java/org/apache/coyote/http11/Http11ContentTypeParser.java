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
        throw new IllegalArgumentException("지원하지 않는 ContentType입니다.");
    }
}
