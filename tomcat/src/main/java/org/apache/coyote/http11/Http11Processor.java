package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

            final List<String> request = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .takeWhile(line -> !line.isEmpty())
                    .collect(Collectors.toList());

            final HttpRequest httpRequest = new HttpRequest(request);

            if (httpRequest.getMethod() == HttpMethod.GET && httpRequest.getRequestUri().contains(".")) {
                generateResourceResponse(httpRequest, outputStream);
                return;
            }

            final String path = httpRequest.getPath();
            if (httpRequest.getMethod() == HttpMethod.GET && path.equals("/login")) {
                generateLoginResponse(httpRequest, outputStream);
                return;
            }
            generateDefaultResponse(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void generateLoginResponse(final HttpRequest httpRequest, final OutputStream outputStream) throws IOException {
        final QueryString queryString = httpRequest.getQueryString();
        final String account = queryString.getValue("account");
        final String password = queryString.getValue("password");
        final User loginUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow();
        log.info("login user: {}", loginUser);

        final String response = new HttpResponse(HttpStatus.FOUND)
                .addLocation("/index.html")
                .build();

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private static void generateDefaultResponse(final OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = new HttpResponse(HttpStatus.OK)
                .addContentType(ContentType.HTML)
                .addContentLength(responseBody.length())
                .build(responseBody);

        log.info("response: {}", response);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void generateResourceResponse(final HttpRequest request, final OutputStream outputStream) throws IOException {
        final String requestUri = request.getRequestUri();
        final String extension = requestUri.substring(requestUri.lastIndexOf(".") + 1);
        final ContentType contentType = ContentType.of(extension);
        final var resource = getClass().getClassLoader().getResource("static/" + requestUri);

        final String responseBody = Files.readString(Path.of(Objects.requireNonNull(resource).getPath()));

        final var response = new HttpResponse(HttpStatus.OK)
                .addContentType(contentType)
                .addContentLength(responseBody.length())
                .build(responseBody);

        log.info("response: {}", response);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
