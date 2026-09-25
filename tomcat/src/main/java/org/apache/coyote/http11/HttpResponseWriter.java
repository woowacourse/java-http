package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http.response.HttpResponse;

public class HttpResponseWriter {

    private static final String CRLF = "\r\n";

    public void write(HttpResponse response, OutputStream outputStream) throws IOException {
        final StringBuilder header = new StringBuilder()
                .append(response.statusLine().value()).append(CRLF);

        response.headers().getValues().forEach((name, value) ->
                header.append(name).append(": ").append(value).append(CRLF));
        header.append(CRLF);

        outputStream.write(header.toString().getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(response.body().bytes());
        outputStream.flush();
    }
}
