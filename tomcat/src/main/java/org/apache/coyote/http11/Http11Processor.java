package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;

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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = new HttpRequest(bufferedReader);
            String statusCode = "200 OK";

            String path = httpRequest.getPath();

            if (path.equals("/login") && httpRequest.hasParam()) {
                Map<String, String> queryParams = httpRequest.getQueryParams();
                Optional<User> user = InMemoryUserRepository.findByAccount(queryParams.getOrDefault("account", ""));

                HttpResponse httpResponse = getHttpResponse(queryParams, user);

                outputStream.write(httpResponse.getBytes());
                outputStream.flush();
                return;
            }

            String responseBody = getResponseBody(path);
            String contentLength = Integer.toString(responseBody.getBytes().length);

            Map<String, String> responseHeader = new LinkedHashMap<>();
            responseHeader.put("Content-Type", getContentType(path));
            responseHeader.put("Content-Length", contentLength);

            HttpResponse httpResponse = HttpResponse.from("HTTP/1.1", statusCode, responseHeader, responseBody);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getHttpResponse(Map<String, String> queryParams, Optional<User> user) {
        String password = queryParams.getOrDefault("password", "");

        if (user.isPresent() && user.get().checkPassword(password)) {
            Map<String, String> responseHeader = Map.of("Location", "/index.html");
            return HttpResponse.from("HTTP/1.1", "302 FOUND", responseHeader);
        }

        Map<String, String> responseHeader = Map.of("Location", "/401.html");
        return HttpResponse.from("HTTP/1.1", "302 FOUND", responseHeader);
    }

    private String getResponseBody(String input) throws URISyntaxException, IOException {
        String responseBody = "Hello world!";

        if (!input.equals("/")) {
            if (input.equals("/login")) {
                input = input + ".html";
            }
            Path path = Paths.get(getClass()
                .getClassLoader()
                .getResource("static" + input)
                .toURI());

            responseBody = new String(Files.readAllBytes(path));
        }

        return responseBody;
    }

    private String getContentType(String uri) {
        String contentType = "text/html;charset=utf-8";

        if (uri.endsWith("css")) {
            contentType = "text/css,*/*;q=0.1";
        }
        return contentType;
    }
}
