package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponseWriter {

    public void write(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.asHeaderString().getBytes());
        outputStream.write(response.body());
        outputStream.flush();
    }
}
