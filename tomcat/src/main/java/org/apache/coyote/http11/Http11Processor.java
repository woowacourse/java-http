package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final List<HttpRequestHandler> requestHandlers = List.of(
            new BasicURIHandler(),
            new IndexPageHandler(),
            new IndexCSSHandler(),
            new HttpJavascriptHandler(),
            new HttpAssetHandler(),
            new LoginPageHandler()
    );

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
        final HttpRequest httpRequest = HttpRequest.from(connection);

        try (final OutputStream outputStream = connection.getOutputStream()) {
            for (HttpRequestHandler requestHandler : this.requestHandlers) {
                if (requestHandler.support(httpRequest)) {
                    requestHandler.handle(httpRequest, outputStream);
                }
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
