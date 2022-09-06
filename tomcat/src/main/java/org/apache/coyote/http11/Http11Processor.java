package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.support.ApiHandlerMethod;
import org.apache.coyote.support.HttpRequest;
import org.apache.coyote.support.StaticHandlerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private Socket connection;

    public Http11Processor() {
    }

    public Http11Processor(final Socket connection) {
        this.connection = connection;
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

            if (route(bufferedWriter, httpRequest)) {
                return;
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean route(final BufferedWriter bufferedWriter, final HttpRequest httpRequest) {
        if (httpRequest.isEmpty()) {
            log.info("isEmpty = {}", httpRequest);
            return true;
        }
        ApiHandlerMethod apiHandlerMethod = ApiHandlerMethod.find(httpRequest);
        if (apiHandlerMethod != null) {
            log.info("API Request = {}", httpRequest);
            apiHandlerMethod.handle(httpRequest, bufferedWriter);
            return true;
        }
        log.info("View Request = {}", httpRequest);
        StaticHandlerMethod.INSTANCE.handle(httpRequest, bufferedWriter);
        return false;
    }
}
