package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.HttpRequest;
import org.apache.HttpResponse;
import org.apache.coyote.Processor;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = new HttpRequest(inputStream);

            log.info("[ REQUEST START LINE : {} ]", httpRequest.getStartLine());

            if (httpRequest.getUri().equals("/")) {
                returnIndexPage(outputStream);
                return;
            }
            if (httpRequest.isStaticResourceRequest()) {
                returnStaticResource(outputStream, httpRequest);
                return;
            }
            if (httpRequest.isQueryString()) {
                Map<String, String> queryString = httpRequest.parseQueryString();
                Optional<User> user = InMemoryUserRepository.findByAccount(queryString.get("account"));
                if (!user.isEmpty() && user.get().checkPassword(queryString.get("password"))) {
                    log.info("[ {} 계정이 존재합니다. : {} ]", queryString.get("account"), user.get());
                }
                returnStaticResource(outputStream, HttpRequest.of("/login.html"));
                return;
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void returnIndexPage(final OutputStream outputStream) throws IOException {
        String body = "Hello world!";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "text/html;charset=utf-8");
        headerMap.put("Content-Length", String.valueOf(body.getBytes().length));
        HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1", "200 OK", headerMap, body
        );

        sendResponse(outputStream, httpResponse);
    }

    private void returnStaticResource(
            final OutputStream outputStream, final HttpRequest httpRequest
    ) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + httpRequest.getUri());
        Path resourcePath = Paths.get(resource.getPath());
        String body = Files.readString(resourcePath);
        String httpStatusCode = "200 OK";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "text/" + httpRequest.getStaticResourceType() + ";charset=utf-8");
        headerMap.put("Content-Length", String.valueOf(body.getBytes().length));
        HttpResponse httpResponse = new HttpResponse(
                "HTTP/1.1", httpStatusCode, headerMap, body
        );

        sendResponse(outputStream, httpResponse);
    }

    private void sendResponse(
            final OutputStream outputStream, final HttpResponse httpResponse
    ) throws IOException {
        outputStream.write(httpResponse.createFullMessage().getBytes());
        outputStream.flush();
    }
}
