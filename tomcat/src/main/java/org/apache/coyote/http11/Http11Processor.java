package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.visitor.Visitor;
import nextstep.jwp.model.visitor.VisitorManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.Http11HandlerSelector;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Http11HandlerSelector http11HandlerSelector;
    private final VisitorManager visitorManager;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.http11HandlerSelector = new Http11HandlerSelector();
        this.visitorManager = new VisitorManager();
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
            Http11Request http11Request = Http11Request.of(bufferedReader);
            Visitor visitor = visitorManager.identify(http11Request.getSessionId());

            log.info(http11Request.getUri());
            Http11Handler http11Handler = http11HandlerSelector.getHttp11Handler(http11Request);
            Http11Response http11Response = http11Handler.handle(http11Request, visitor);

            if (visitor.isNewVisitor()) {
                http11Response.setSession(visitor.getSessionId());
            }
            final var response = http11Response.toResponseFormat();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
