package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HandlerMapper;
import org.apache.coyote.http11.error.ErrorHandlerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ResponseResolver;
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

            String response = makeResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
        }
    }

    private String makeResponse(HttpRequest httpRequest) throws IOException {
        RequestLine requestLine = httpRequest.getRequestLine();
        try {
            if (HandlerMapper.hasHandler(requestLine.getRequestURI())) {
                return resolveHandlerResponse(httpRequest);
            }
            return ResponseResolver.resolveViewResponse(
                    requestLine.getRequestURI(),
                    findResponseContentType(requestLine.getRequestURI())
            );
        } catch (Exception e) {
            if (ErrorHandlerMapper.hasErrorHandler(e.getClass())) {
                return resolveErrorHandlerResponse(requestLine, e);
            }
            throw new UncheckedServletException(e);
        }
    }

    private String resolveErrorHandlerResponse(RequestLine requestLine, Exception e) {
        Map<String, String> parameters = ErrorHandlerMapper.handleError(e.getClass());
        return ResponseResolver.resolveResponse(
                parameters,
                findResponseContentType(requestLine.getRequestURI())
        );
    }

    private String resolveHandlerResponse(HttpRequest httpRequest) {
        Controller controller = HandlerMapper.mapTo(httpRequest.getRequestUri());
        Map<String, String> responseParameters = controller.handle(httpRequest);
        return ResponseResolver.resolveResponse(
                responseParameters,
                findResponseContentType(httpRequest.getRequestUri())
        );
    }

    private String findResponseContentType(String url) {
        String[] extension = url.split("\\.");
        if (extension.length < 2) {
            return "text/html";
        }
        return "text/" + extension[1];
    }
}
