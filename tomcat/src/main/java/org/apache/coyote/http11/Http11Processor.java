package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestReader;
import org.apache.coyote.response.ResponseEntity;
import org.apache.front.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final Proxy proxy;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.proxy = new Proxy();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final Request request = Request.from(RequestReader.from(bufferedReader));
            final ResponseEntity responseEntity = proxy.process(request);
            writeResponse(outputStream, responseEntity);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(final OutputStream outputStream, final ResponseEntity responseEntity) throws IOException {
        outputStream.write(responseEntity.toString().getBytes());
        outputStream.flush();
    }
}
