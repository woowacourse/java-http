package org.apache.coyote.http11;

import static support.IoUtils.writeAndFlush;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.ControllerContainer;
import org.apache.coyote.handler.StaticHandlerMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
             final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            final HttpRequest request = new HttpRequest(bufferedReader);
            final HttpResponse response = new HttpResponse();

            route(request, response);
            writeAndFlush(bufferedWriter, response.toStringData());
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void route(final HttpRequest request, final HttpResponse response) {
        final Controller controller = ControllerContainer.findFromUri(request.getUri(), request.getHttpMethod());
        if (controller != null) {
            controller.service(request, response);
        }
        StaticHandlerMethod.INSTANCE.handle(request, response);
    }

}
