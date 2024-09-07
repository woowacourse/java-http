package org.apache.coyote.http11.message.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.StringJoiner;

public class HttpResponseWriter {

    private static final String CRLF = "\r\n";

    private final OutputStream outputStream;

    public HttpResponseWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    // TODO: body는 바이너리 데이터 일 수 있기 때문에 따로 처리해야 한다.
    public void write(HttpResponse response) throws IOException {
        String statusLine =
                String.format("HTTP/1.1 %d %s", response.getStatus().getCode(), response.getStatus().getReasonPhrase());
        String headerLines = String.join(CRLF, response.getHeaderLines());
        String responseMessage = new StringJoiner(CRLF)
                .add(statusLine)
                .add(headerLines)
                .add("")
                .add(response.getBody())
                .toString();

        outputStream.write(responseMessage.getBytes());
        outputStream.flush();
    }
}
