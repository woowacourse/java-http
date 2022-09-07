package support;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.http.HttpResponse;

public class HttpFactory {

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


    public static HttpResponse create() {
        return HttpResponse.from(
                new BufferedWriter(
                        new OutputStreamWriter(
                                new ByteArrayOutputStream())));
    }
}
