package org.apache.coyote.http11;

import com.techcourse.exception.HttpStatusException;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import com.spring.http.request.HttpRequest;
import com.spring.http.response.HttpResponse;
import org.apache.catalina.servlet.HttpServletContainer;
import org.apache.coyote.Processor;
import org.apache.coyote.util.HttpRequestParser;
import org.apache.coyote.util.HttpResponseParser;
import org.apache.coyote.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String DEFAULT_VERSION = "HTTP/1.1";
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final HttpResponse response = processResponse(reader);

            final byte[] output = HttpResponseParser.parse(response);
            outputStream.write(output);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse processResponse(BufferedReader reader) throws IOException {
        HttpResponse response = new HttpResponse(DEFAULT_VERSION);
        try {
            final HttpRequest request = HttpRequestParser.parse(reader);
            response = new HttpResponse(request);
            
            HttpServletContainer.handle(request, response);
            return response;
        } catch (HttpStatusException e) {
            log.error("HttpStatusException 발생 = {}", e.getMessage(), e);
            processResponse(null, response, e);
            return response;
        } catch (IllegalStateException | IllegalArgumentException e) {
            ResponseUtil.handleBadRequest(null, response);
            return response;
        }
    }

    private static void processResponse(HttpRequest request, HttpResponse response, HttpStatusException exception)
            throws IOException {
        log.error("HttpStatusException 발생 = {}", exception.getMessage(), exception);

        response.setStatus(exception.getHttpStatus());
        ResponseUtil.handleErrorPage(request, response);
    }
}
