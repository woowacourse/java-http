package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {

    private final OutputStream connectionOutputStream;
    private final String response;

    public HttpResponse(final OutputStream connectionOutputStream, final String response) {
        this.connectionOutputStream = connectionOutputStream;
        this.response = response;
    }

    public static HttpResponse htmlResourceOkResponse(OutputStream outputStream, String responseBody) {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new HttpResponse(outputStream, response);
    }

    public void flush() throws IOException {
        connectionOutputStream.write(response.getBytes());
        connectionOutputStream.flush();
    }
}
