package org.apache.coyote.http11;

import static org.apache.coyote.HttpStatus.BAD_REQUEST;
import static org.apache.coyote.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.HttpStatus.METHOD_NOT_ALLOWED;
import static org.apache.coyote.HttpStatus.NOT_FOUND;

import com.techcourse.exception.UnauthorizedException;
import com.techcourse.handler.DefaultHandler;
import com.techcourse.handler.LoginHandler;
import com.techcourse.handler.RegisterHandler;
import com.techcourse.handler.RootHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.catalina.request.ServletRequest;
import org.apache.catalina.response.ServletResponse;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpRequestHandler;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String PROTOCOL = "HTTP/1.1";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Map<String, HttpRequestHandler> handlerMap;
    private final HttpRequestHandler defaultHandler;
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.handlerMap = new HashMap<>();
        handlerMap.put("/", new RootHandler());
        handlerMap.put("/login", new LoginHandler());
        handlerMap.put("/register", new RegisterHandler());
        this.defaultHandler = new DefaultHandler();
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
            HttpResponse response = processRequest(inputStream);

            writeResponse(response, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse processRequest(InputStream inputStream) {
        final ServletResponse response = new ServletResponse(PROTOCOL);
        try {
            final ServletRequest request = new ServletRequest(new HttpRequest(inputStream));

            handleRequest(request, response);
        } catch (IllegalArgumentException e) {
            updateResponseWithError(BAD_REQUEST, response, e);
            log.warn("잘못된 요청 형식: {}", e.getMessage());
        } catch (UnauthorizedException e) {
            response.sendRedirect("401.html");
            log.warn(e.getMessage());
        } catch (NoSuchElementException e) {
            response.sendRedirect("404.html");
            log.warn("존재하지 않는 리소스: {}", e.getMessage());
        } catch (UnsupportedOperationException e) {
            updateResponseWithError(METHOD_NOT_ALLOWED, response, e);
            log.warn("지원하지 않는 HTTP 메서드: {}", e.getMessage());
        } catch (Exception e) {
            response.sendRedirect("500.html");
            log.error("예상치 못한 서버 오류 발생", e);
        }
        return response.toHttpResponse();
    }

    private void writeResponse(final HttpResponse response, OutputStream outputStream) throws IOException {
        outputStream.write(response.getResponse().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private void handleRequest(ServletRequest request, ServletResponse response) {
        final HttpRequestHandler handler = handlerMap.getOrDefault(request.getPath(), defaultHandler);

        switch (request.getMethod()) {
            case "GET" -> handler.handleGet(request, response);
            case "POST" -> handler.handlePost(request, response);
            default -> throw new UnsupportedOperationException("지원하지 않는 요청 방식입니다: " + request.getMethod());
        }
    }

    private void updateResponseWithError(HttpStatus status, ServletResponse response, Exception e) {
        response.setStatus(status);
        response.setBody(e.getMessage());
        response.setContentType("text/plain;charset=utf-8");
    }
}
