package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestControllerMapper;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.HttpMimeType;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.Http11ResponseHeaders;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            Http11Request request = Http11Request.from(inputStream);
            Http11Response response = Http11Response.ok();

            // 여기부터 response 만들기
            processNonStaticRequest(request, response);
            processStaticRequest(request, response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processNonStaticRequest(Http11Request request, Http11Response response) {
        if (request.isStaticRequest()) {
            return;
        }

        Controller controller = RequestControllerMapper.getController(request.getUri());
        if (controller == null) {
            response.setStatusCode(HttpStatusCode.NOT_FOUND);
            request.setUri("/404.html");
            return;
        }
        try {
            controller.service(request, response);
        } catch (Exception e) {
            response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
            request.setUri("/500.html");
        }
    }

    private void processStaticRequest(Http11Request request, Http11Response response) throws IOException {
        if (!request.isStaticRequest()) {
           return;
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = loader.getResourceAsStream("static/" + request.getUri())) {
            if (stream == null) {
                response.setStatusCode(HttpStatusCode.NOT_FOUND);
                return;
            }
            String responseBody = new String(stream.readAllBytes());
            response.setResponseBody(responseBody);
            response.setHeaders(Http11ResponseHeaders.builder()
                    .addHeader("Content-Type", HttpMimeType.from(getExtension(request)).asString())
                    .addHeader("Content-Length", String.valueOf(responseBody.getBytes().length))
                    .build());
        }
    }

    private static String getExtension(Http11Request request) {
        if (!request.isStaticRequest()) {
            return null;
        }

        int index = request.getUri().lastIndexOf(".");
        String fileExtension = request.getUri().substring(index + 1);
        if (fileExtension.equals("js")) {
            fileExtension = "javascript";
        }
        return fileExtension;
    }
}
