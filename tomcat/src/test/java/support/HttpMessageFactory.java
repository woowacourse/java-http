package support;

import java.nio.charset.StandardCharsets;

public class HttpMessageFactory {

    public static String get(final String requestTarget) {
        return String.join("\r\n",
                "GET " + requestTarget + " HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");
    }

    public static String getWithCookie(final String requestTarget, final String cookie) {
        return String.join("\r\n",
                "GET " + requestTarget + " HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cookie: " + cookie,
                "",
                "");
    }

    public static String post(final String requestTarget, final String messageBody) {
        return String.join("\r\n",
                "POST " + requestTarget + " HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: " + messageBody.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                messageBody);
    }
}
