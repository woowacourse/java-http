package org.apache.coyote.http11;

import static org.apache.coyote.http11.common.Status.OK;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            log.info("process start");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final var request = Request.read(bufferedReader)
                    .orElseThrow(() -> new IllegalStateException("invalid request"));
            final var requestURI = request.getURI();
            log.info("request: {}", request);

            // TODO 요청 URI 구분하여 다르게 동작하게 하기
            if ("/".equals(requestURI)) {
                final var response = Response.of(OK, "text/html", "Hello world!").toString();

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            final var resource = getClass().getClassLoader().getResource("static" + requestURI);
            assert resource != null;
            log.info("resource found : {}", resource.getPath());

            final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final var response = Response.of(OK, "text/html", responseBody).toString();

            outputStream.write(response.getBytes());
            log.info("write response: {}", response);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

}
