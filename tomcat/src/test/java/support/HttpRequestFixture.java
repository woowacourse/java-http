package support;

import org.apache.coyote.http11.request.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpRequestFixture {

    public static HttpRequest get(String path) throws IOException {
        return request("GET", path);
    }

    public static HttpRequest getWithCookie(String path, String cookie) throws IOException {
        return from(String.join("\r\n",
                "GET " + path + " HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: " + cookie,
                "",
                ""));
    }

    public static HttpRequest post(String path, String body) throws IOException {
        return from(String.join("\r\n",
                "POST " + path + " HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body));
    }

    public static HttpRequest request(String method, String path) throws IOException {
        return from(String.join("\r\n",
                method + " " + path + " HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""));
    }

    private static HttpRequest from(String raw) throws IOException {
        return HttpRequest.from(new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8)));
    }

}
