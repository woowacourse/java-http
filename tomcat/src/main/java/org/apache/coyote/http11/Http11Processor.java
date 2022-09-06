package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.dto.ResponseComponent;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.Http11HandlerSelector;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11request.Http11RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Http11RequestHandler http11RequestHandler;
    private final Http11HandlerSelector http11HandlerSelector;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.http11RequestHandler = new Http11RequestHandler();
        this.http11HandlerSelector = new Http11HandlerSelector();
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {
            Http11Request http11Request = http11RequestHandler.makeRequest(bufferedReader);

            log.info(http11Request.getUri());
            Http11Handler http11Handler = http11HandlerSelector.getHttp11Handler(http11Request);
            ResponseComponent responseComponent = http11Handler.handle(http11Request);

            final var response = responseComponent.toString();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
