package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.controller.Controller;
import org.apache.controller.HandlerContainer;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestStartLine;
import org.apache.coyote.http11.response.Http11Response;
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

            Http11Request request = Http11Request.from(inputStream);
            log.info("http request : {}", request);
            Http11Response response = Http11Response.create();

            handle(request, response);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handle(Http11Request request, Http11Response response) throws IOException {
        try {
            String endpoint = request.getEndpoint();
            if (endpoint.equals("/")) {
                response.addBody("Hello world!");
                response.addContentType("text/html");
                return;
            }
            if (isStaticResourceRequest(request.getStartLine())) {
                response.addStaticBody(endpoint);
                return;
            }

            Controller handler = HandlerContainer.getHandlers().stream()
                    .filter(controller -> controller.isMatch(endpoint))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핸들러입니다."));

            handler.service(request, response);
        } catch (IllegalArgumentException e) {
            response.sendRedirect("/404.html");
        } catch (Exception e) {
            response.sendRedirect("/500.html");
        }
    }

    private boolean isStaticResourceRequest(Http11RequestStartLine startLine) {
        return startLine.getMethod() == HttpMethod.GET && startLine.getEndPoint().contains(".");
    }
}
