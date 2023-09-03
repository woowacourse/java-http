package org.apache.coyote.http11;

import static org.apache.catalina.servlet.response.HttpStatus.NOT_FOUND;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.catalina.servlet.handler.RequestHandler;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.request.StartLine;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpRequestParser parser = new HttpRequestParser();

    private final List<RequestHandler> requestHandlers;
    private final Socket connection;

    public Http11Processor(List<RequestHandler> requestHandlers, final Socket connection) {
        this.requestHandlers = requestHandlers;
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            HttpRequest request = parser.parse(bufferedReader);
            StartLine startLine = request.startLine();
            if (startLine.isEmpty()) {
                return;
            }
            HttpResponse response = null;
            for (RequestHandler requestHandler : requestHandlers) {
                if (requestHandler.canHandle(request)) {
                    response = requestHandler.handle(request);
                    break;
                }
            }
            if (response == null) {
                response = new HttpResponse(new StatusLine(NOT_FOUND), null, null);
            }

            bufferedWriter.write(response.toString());
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
