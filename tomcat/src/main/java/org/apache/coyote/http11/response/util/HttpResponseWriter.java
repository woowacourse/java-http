package org.apache.coyote.http11.response.util;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.response.HttpResponse;

public class HttpResponseWriter {

    private final OutputStream outputStream;
    private final HttpResponseFormatter formatter = HttpResponseFormatter.getInstance();

    public HttpResponseWriter(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(final HttpResponse response) throws IOException {
        final byte[] bytes = formatter.format(response).getBytes();
        outputStream.write(bytes);
        outputStream.flush();
    }
}
