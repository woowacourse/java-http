package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.handler.ControllerMapper;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String PROCESS_MESSAGE = "connect host: {}, port: {}";

    private final Socket connection;

    private final ControllerMapper controllerMapper;

    public Http11Processor(final Socket connection, final ControllerMapper controllerMapper) {
        this.connection = connection;
        this.controllerMapper = controllerMapper;
    }

    @Override
    public void run() {
        log.info(PROCESS_MESSAGE, connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final Request request = Request.from(bufferedReader);
            final String response = createResponse(request);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createResponse(final Request request) throws Exception {
        final Controller controller = controllerMapper.findHandler(request.getRequestLine());
        final Response response = Response.DEFAULT;
        controller.service(request, response);
        return response.getResponse();
    }
}
