package org.apache.coyote.http11;

public class HttpResponse {
    public static byte[] ok(final String fileType, final String responseBody) {
        return String.join("\r\n", "HTTP/1.1 200 OK ",
                "Content-Type: " + fileType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        ).getBytes();
    }

    public static byte[] found(final String location) {
        return String.join("\r\n", "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                ""
        ).getBytes();
    }

    public static byte[] found(final String location, final HttpCookie cookie) {
        return String.join("\r\n", "HTTP/1.1 302 Found ",
                "Set-Cookie: JSESSIONID=" + cookie.getCookieValue("JSESSIONID") + " ",
                "Location: " + location + " ",
                ""
        ).getBytes();
    }
}
