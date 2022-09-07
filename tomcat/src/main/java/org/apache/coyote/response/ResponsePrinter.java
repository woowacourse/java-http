package org.apache.coyote.response;

import java.io.IOException;
import java.io.OutputStream;
import nextstep.jwp.exception.UncheckedServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponsePrinter {

    private static final Logger log = LoggerFactory.getLogger(ResponsePrinter.class);

    private final OutputStream outputStream;

    public ResponsePrinter(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void flushBuffer(String response) {
        try {
            outputStream.write(response.getBytes());
            outputStream.flush();
        }  catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
