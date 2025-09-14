package org.apache.coyote.http11;

import com.techcourse.exception.MethodNotAllowedException;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestParser;
import org.apache.coyote.http11.response.Http11Response;
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

            final Http11Request request = Http11RequestParser.parse(inputStream);
            final Http11Response response = new Http11Response();

            final Controller controller = RequestMapping.getController(request);
            try {
                controller.service(request, response);
            } catch (final MethodNotAllowedException e) {
                StaticResource staticResource = StaticResourceProvider.getStaticResource("/404.html");
                response.setStaticResource(staticResource);
                response.setHttpStatus(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                StaticResource staticResource = StaticResourceProvider.getStaticResource("/500.html");
                response.setStaticResource(staticResource);
                response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            outputStream.write(response.toResponseBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
