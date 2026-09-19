package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class HttpResponse {

    private final int statusCode;
    private final String reasonPhrase;
    private final String contentType;
    private final byte[] body;

    private HttpResponse(int statusCode, String reasonPhrase, String contentType, byte[] body) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse ok(String contentType, byte[] body) {
        return new HttpResponse(200, "OK", contentType, body);
    }

    public static HttpResponse notFound(String contentType, byte[] body) {
        return new HttpResponse(404, "Not Found", contentType, body);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        String headers = String.join("\r\n",
                "HTTP/1.1 " + statusCode + " " + reasonPhrase + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + body.length + " ",
                "",
                "");
        outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
    }
}
