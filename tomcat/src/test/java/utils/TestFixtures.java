package utils;

import org.apache.coyote.http11.request.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestFixtures {

    public static final String HTTP_VERSION = "HTTP/1.1";
    public static final String CONTENT_TYPE_HTML = "Content-Type: text/html;charset=utf-8";
    public static final String CONTENT_TYPE_CSS = "Content-Type: text/css";
    public static final String CONTENT_TYPE_JAVASCRIPT = "Content-Type: application/javascript";
    public static final String CONTENT_TYPE_SVG = "Content-Type: image/svg+xml";
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String NEW_LINE = "\r\n";

    public static HttpRequest buildHttpRequest(String method, String path, String body) throws IOException {
        String requestLine = String.join(" ",
                method,
                path,
                "HTTP/1.1");

        String request = String.join("\r\n",
                requestLine,
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "Connection: keep-alive",
                "",
                body);

        return new HttpRequest(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)));
    }
}
