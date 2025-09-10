package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.application.Handler;
import org.apache.coyote.http11.application.ViewResolver;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.request.Api;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.controller.UserController;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private final List<Handler> handlers = List.of(
        new UserController(SESSION_MANAGER)
    );
    private final ViewResolver viewResolver = new ViewResolver();
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
            HttpRequest request = new HttpRequest(inputStream);
            HttpResponse response = new HttpResponse();

            try {
                Api api = request.getApi();
                for (var handler : handlers) {
                    var handlerMethod = handler.getHandlerMethod(api);
                    if (handlerMethod != null) {
                        response = handlerMethod.apply(request);
                        break;
                    }
                }
            } catch (UnauthorizedException e) {
                request.setPath("/401.html");
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.getHeaders().clear();
            } catch (IllegalArgumentException e) {
                request.setPath("/404.html");
                response.setStatus(HttpStatus.NOT_FOUND);
                response.getHeaders().clear();
            }

            viewResolver.resolve(request, response);
            final var output = response.buildResponse();
            outputStream.write(output.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
