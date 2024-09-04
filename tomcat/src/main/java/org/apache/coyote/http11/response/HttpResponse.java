package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {

    private final String response;

    public HttpResponse(final String response) {
        this.response = response;
    }

    public static HttpResponse ok(String responseBody, String contentType) {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new HttpResponse(response);
    }

    public void flush(OutputStream outputStream) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
