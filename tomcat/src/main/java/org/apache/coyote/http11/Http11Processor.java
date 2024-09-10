package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final RequestMappings requestMappings;

    public Http11Processor(final Socket connection, RequestMappings requestMappings) {
        this.connection = connection;
        this.requestMappings = requestMappings;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (connection) {
            sendResponse(connection);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendResponse(Socket connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        OutputStream outputStream = connection.getOutputStream();
        HttpRequest request = HttpRequest.from(inputStream);

        String requestURI = request.requestUri();

        Controller controller = requestMappings.findController(requestURI);
        HttpResponse response = new HttpResponse();
        controller.service(request, response);
        outputStream.write(response.toBytes());
        outputStream.flush();
    }
}
