package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;
import static support.IoUtils.writeAndFlush;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import nextstep.jwp.exception.MemberAlreadyExistException;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.HandlerMapping;
import org.apache.coyote.controller.SingletonContainer;
import org.apache.coyote.handler.StaticHandlerMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HandlerMapping handlerMapping;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        handlerMapping = SingletonContainer.getObject(HandlerMapping.class);
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        HttpRequest request = null;
        HttpResponse response = null;

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
             final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8))) {

            request = new HttpRequest(bufferedReader);
            response = new HttpResponse();

            route(request, response, bufferedWriter);
            writeAndFlush(bufferedWriter, response.toStringData());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void route(final HttpRequest request, final HttpResponse response, final BufferedWriter bufferedWriter) {
        try {
            routeInternal(request, response);
        } catch (Exception e) {
            exceptionResolve(e, response);
            writeAndFlush(bufferedWriter, response.toStringData());
        }
    }

    private void routeInternal(final HttpRequest request, final HttpResponse response) {
        final Controller controller = handlerMapping.findFromUri(request.getUri(), request.getHttpMethod());
        if (isApiRequest(controller)) {
            controller.service(request, response);
            return;
        }
        StaticHandlerMethod.INSTANCE.handle(request, response);
    }

    private void exceptionResolve(final Exception e, final HttpResponse response) {
        log.error(e.getMessage(), e);
        if (e instanceof MemberAlreadyExistException) {
            response.sendRedirect("/401");
            return;
        }
    }

    private boolean isApiRequest(final Controller controller) {
        return controller != null;
    }

}
