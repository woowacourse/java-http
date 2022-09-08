package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final String protocolVersion = "HTTP/1.1";
    private String statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this.headers = new HashMap<>();
    }

    private HttpResponse(final String statusCode, final String statusMessage,
                         final Map<String, String> headers, final String body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse create200Response(final Map<String, String> headers, final String body) {
        this.setStatusCode("200");
        this.setStatusMessage("OK");
        this.setHeaders(headers);
        this.setBody(body);

        return this;
    }

    public HttpResponse create302Response(final Map<String, String> headers, final String body) {
        this.setStatusCode("302");
        this.setStatusMessage("Found");
        this.setHeaders(headers);
        this.setBody(body);

        return this;
    }

    public HttpResponse create401Response() throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final Path path = new File(resource.getFile()).toPath();
        final String responseBody = new String(Files.readAllBytes(path));

        return this.create200Response(Map.of("Content-Type", Files.probeContentType(path)), responseBody);
    }

    public byte[] toBytes() {
        return createResponse().getBytes();
    }

    private String createResponse() {
        if (headers.containsKey("Set-Cookie")) {
            return String.join("\r\n",
                    "HTTP/1.1 " + statusCode + " " + statusMessage + " ",
                    "Content-Type: " + headers.get("contentType") + ";charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "Set-Cookie: JSESSIONID=" + headers.get("Set-Cookie") + " ",
                    "",
                    body);
        }

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " " + statusMessage + " ",
                "Content-Type: " + headers.get("contentType") + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    public void addCookie(String cookie) {
        headers.put("Set-Cookie", cookie);
    }

    public void setStatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMessage(final String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public void setBody(final String body) {
        this.body = body;
    }
}

