package org.apache.coyote.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseFlusher {

    private static final Logger log = LoggerFactory.getLogger(ResponseFlusher.class);

    private ResponseFlusher() {
    }

    public static void flush(final Response response) {
        final OutputStream outputStream = response.getOutputStream();
        if (outputStream == null) {
            return;
        }
        try {
            outputStream.write(response.parse().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
