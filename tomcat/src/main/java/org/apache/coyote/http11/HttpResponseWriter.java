package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpResponseWriter {

    public void write(final OutputStream outputStream, final HttpResponse response) throws IOException {
        Map<String, List<String>> headers = response.headers();
        byte[] body = response.body();
        headers.putIfAbsent("Content-Type", new ArrayList<>(List.of("text/html;charset=utf-8")));
        headers.putIfAbsent("Content-Length", new ArrayList<>(List.of(String.valueOf(body.length))));

        outputStream.write(response.asString().getBytes());
        outputStream.flush();
    }
}
