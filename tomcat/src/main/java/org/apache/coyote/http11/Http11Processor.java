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
import org.apache.coyote.handler.ApiHandlerMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.handler.StaticHandlerMethod;
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

            final HttpRequest httpRequest = new HttpRequest(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse();
            route(httpRequest, httpResponse, bufferedWriter);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void route(final HttpRequest request, final HttpResponse response,
                       final BufferedWriter bufferedWriter) {
        ApiHandlerMethod apiHandlerMethod = ApiHandlerMethod.find(request);
        if (apiHandlerMethod != null) {
            log.info("API Request = {}", request);
            apiHandlerMethod.handle(request, response);
        } else {
            log.info("View Request = {}", request);
            StaticHandlerMethod.INSTANCE.handle(request, response);
        }
        writeAndFlush(bufferedWriter, response.toStringData());
    }
}
