package org.apache.coyote.util;

public class HttpResponseUtil {

    private static final String NEXT_LINE = "\r\n";

    private HttpResponseUtil() {
    }

    public static String generate(final String path, final String responseBody) {
        return String.join(
                NEXT_LINE,
                "HTTP/1.1 200 OK ",
                "Content-Type: " + getContentType(path) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private static String getContentType(final String url) {
        if (url.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
