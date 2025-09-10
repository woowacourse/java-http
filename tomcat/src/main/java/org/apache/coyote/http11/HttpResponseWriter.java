package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HttpResponseWriter {

    public void write(final OutputStream outputStream, final HttpResponse response) throws IOException {
        Map<String, String> headers = response.headers();
        byte[] body = response.body();
        headers.putIfAbsent("Content-Type", "text/html;charset=utf-8");
        headers.putIfAbsent("Content-Length", String.valueOf(body.length));

        outputStream.write(response.asHeaderString().getBytes());
        outputStream.write(response.body());
        outputStream.flush();
    }
}
