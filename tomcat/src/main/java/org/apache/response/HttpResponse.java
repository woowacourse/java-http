package org.apache.response;

import org.apache.common.HttpStatus;

public class HttpResponse {

    public static String create(String content, String url, HttpStatus httpStatus) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getCode() + httpStatus.name() + "",
                "Content-Type: " + getContentType(url) + ";charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
    }

    private static String getContentType(final String url) {
        if (url.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
