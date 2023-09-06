package org.apache.coyote.http11;

import org.apache.coyote.http11.cookie.HttpCookie;

public class Response {

    public static String of(final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + "charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public static String of(final String contentType, final String responseBody, final String status) {
        return String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + contentType + "charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public static String of(final String contentType, final String responseBody, final HttpCookie httpCookie) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Set-Cookie: " + "JSESSIONID=" + httpCookie.getJSessionId() + " ",
                "Content-Type: " + contentType + "charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
