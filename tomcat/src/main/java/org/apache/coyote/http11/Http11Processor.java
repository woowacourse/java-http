package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HandlerMapper;
import org.apache.coyote.http11.error.ErrorHandlerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse response = makeResponse(httpRequest);

            outputStream.write(response.serialize().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse makeResponse(HttpRequest httpRequest) throws IOException {
        try {
            if (HandlerMapper.hasHandler(httpRequest.getRequestUri())) {
                return resolveHandlerResponse(httpRequest);
            }
            return resolveViewResponse(httpRequest);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    private HttpResponse handleError(Exception e) {
        if (ErrorHandlerMapper.hasErrorHandler(e.getClass())) {
            return ErrorHandlerMapper.handleError(e.getClass());
        }
        throw new UncheckedServletException(e);
    }

    private HttpResponse resolveViewResponse(HttpRequest httpRequest) {
        return HttpResponse.ok(httpRequest.getRequestUri());
    }

    private HttpResponse resolveHandlerResponse(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse();
        Controller controller = HandlerMapper.mapTo(httpRequest.getRequestUri());
        controller.service(httpRequest, httpResponse);
        return httpResponse;
    }
}
