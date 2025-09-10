package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.util.ControllerMapper;
import org.apache.coyote.http11.exception.Http11ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";

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
            final Http11Request request = new Http11Request(inputStream);
            final Http11Response response = new Http11Response(outputStream);

            final Controller controller = ControllerMapper.getController(request.getPath());
            controller.service(request, response);

            outputStream.write(response.buildResponse(StandardCharsets.UTF_8));
            outputStream.flush();
            response.sendError();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (Http11ParseException ex) {
            log.error(ex.getMessage(), ex); // TODO: response.sendError()로 변경
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
