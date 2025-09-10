package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.core.ApplicationProcessor;
import org.apache.coyote.util.StaticResourcePathGenerator;
import org.apache.coyote.Processor;
import org.apache.coyote.util.request.HttpRequest;
import org.apache.coyote.util.request.HttpRequestParser;
import org.apache.coyote.util.response.HttpContentTypeResolver;
import org.apache.coyote.util.response.HttpResponse;
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
        OutputStream outputStream = null;
        try (final var inputStream = connection.getInputStream()) {
            outputStream = connection.getOutputStream();
            HttpRequest request = HttpRequestParser.parse(inputStream);
            if (request == null) {
                respond(HttpResponse.notFound(), outputStream);
                return;
            }
            if (handleApiRequest(request, outputStream)) {
                return;
            }
            if (handleStaticResourceRequest(request.getPath(), outputStream)) {
                return;
            }
            respond(HttpResponse.notFound(), outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
            handleError(outputStream, e);
        }
    }

    private void handleError(OutputStream outputStream, Exception e) {
        try {
            respond(HttpResponse.internalServerError(), outputStream);
        } catch (IOException ex) {
            log.error("Error response failed: {}", ex.getMessage(), ex);
        }
    }

    private boolean handleApiRequest(HttpRequest request, OutputStream outputStream) throws IOException {
        if ("/login".equals(request.getPath())) {
            HttpResponse loginResponse = ApplicationProcessor.processLogin(request);
            respond(loginResponse, outputStream);
            return true;
        }
        if ("/register".equals(request.getPath()) && "POST".equals(request.getMethod())) {
            HttpResponse registerResponse = ApplicationProcessor.processRegister(request);
            respond(registerResponse, outputStream);
            return true;
        }
        return false;
    }

    private boolean handleStaticResourceRequest(String requestPath, OutputStream outputStream) throws IOException {
        String resourcePath = StaticResourcePathGenerator.generate(requestPath);
        if (resourcePath == null) {
            return false;
        }
        byte[] resourceBody = readPathFile(resourcePath);
        if (resourceBody == null) {
            return false;
        }
        respond(HttpResponse.of(
                "HTTP/1.1 200 OK",
                HttpContentTypeResolver.resolve(resourcePath),
                resourceBody
        ), outputStream);
        return true;
    }

    private byte[] readPathFile(String requestPath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(requestPath)) {
            if (inputStream == null) {
                return null;
            }
            return inputStream.readAllBytes();
        }
    }

    private void respond(HttpResponse httpResponse, OutputStream outputStream) throws IOException {
        outputStream.write(httpResponse.createHeader().getBytes());
        outputStream.write(httpResponse.getBody());
        outputStream.flush();
    }
}
