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
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBuilder;
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

            HttpResponse response = makeResponse2(httpRequest);

            outputStream.write(response.serialize().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
        }
    }

    private HttpResponse makeResponse2(HttpRequest httpRequest) throws IOException {
        RequestLine requestLine = httpRequest.getRequestLine();
        try {
            if (HandlerMapper.hasHandler(requestLine.getRequestURI())) {
                return resolveHandlerResponse2(httpRequest);
            }
            return new ResponseBuilder()
                    .statusCode(HttpStatusCode.OK_200)
                    .viewUrl(requestLine.getRequestURI())
                    .build();
        } catch (Exception e) {
            if (ErrorHandlerMapper.hasErrorHandler(e.getClass())) {
                return ErrorHandlerMapper.handleError(e.getClass());
            }
            throw new UncheckedServletException(e);
        }
    }

    private HttpResponse resolveHandlerResponse2(HttpRequest httpRequest) {
        Controller controller = HandlerMapper.mapTo(httpRequest.getRequestUri());
        return controller.handle(httpRequest);
    }
}
