package org.apache.coyote.http11.message.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.StringJoiner;
import org.apache.coyote.http11.message.HttpHeadersStringifier;

public class HttpResponseWriter {

    private static final String CRLF = "\r\n";

    private final OutputStream outputStream;

    public HttpResponseWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(HttpResponse response) throws IOException {
        String statusLine =
                String.format("HTTP/1.1 %d %s", response.getStatus().getCode(), response.getStatus().getReasonPhrase());
        String headerLines = String.join(CRLF, HttpHeadersStringifier.stringify(response.getHeaders()));
        String responseMessage = new StringJoiner(CRLF)
                .add(statusLine)
                .add(headerLines)
                .add("")
                .add("")
                .toString();

        outputStream.write(responseMessage.getBytes());
        outputStream.write(response.getBody());
        outputStream.flush();
    }
}
