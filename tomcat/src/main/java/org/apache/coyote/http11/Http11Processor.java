package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.support.HttpRequest;
import org.apache.coyote.support.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private Socket connection;
    private Router router;

    public Http11Processor() {
    }

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.router = new Router();
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
             final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));) {

            final HttpRequest httpRequest = new HttpRequest(bufferedReader);

            router.route(httpRequest, bufferedReader, bufferedWriter);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
