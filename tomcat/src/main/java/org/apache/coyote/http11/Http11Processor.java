package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import com.techcourse.web.WebApplication;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final WebApplication webApplication;

    public Http11Processor(final Socket connection, final WebApplication webApplication) {
        this.connection = connection;
        this.webApplication = webApplication;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = parseRequest(inputStream);
            if (request == null) {
                return;
            }

            HttpResponse response = webApplication.service(request);
            sendResponse(outputStream, response);
            
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseRequest(final InputStream inputStream) throws IOException {
        try {
            return HttpRequest.from(inputStream);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 HTTP 요청: {}", e.getMessage());
            return null;
        }
    }

    private void sendResponse(final java.io.OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
