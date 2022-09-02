package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
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

            final List<String> request = getRequest(inputStream);
            final HttpRequest httpRequest = new HttpRequest(request);

            HttpResponse httpResponse = getResponse(httpRequest);
            String response = httpResponse.parseToString();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getRequest(final InputStream inputStream) throws IOException {
        final List<String> request = new ArrayList<>();

        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())));
        String line = reader.readLine();

        while (!"".equals(line)) {
            if (Objects.isNull(line)) {
                break;
            }
            request.add(line);
            line = reader.readLine();
        }
        return request;
    }

    public HttpResponse getResponse(HttpRequest httpRequest) throws IOException {
        if (httpRequest.getRequestUri().equals("/")) {
            final String responseBody = "Hello world!";
            return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, ContentType.HTML, responseBody);
        }
        if (httpRequest.getRequestUri().endsWith(".html")) {
            if (httpRequest.getRequestUri().equals("/login.html")) {
                Optional<User> user = InMemoryUserRepository.findByAccount(
                        httpRequest.getQueryStrings().get("account"));
                user.ifPresent(value -> log.debug(value.toString()));
            }
            final String responseBody = getResponseBody(httpRequest);
            return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, ContentType.HTML, responseBody);
        }
        if (httpRequest.getRequestUri().endsWith(".css")) {
            final String responseBody = getResponseBody(httpRequest);
            return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, ContentType.CSS, responseBody);

        }
        if (httpRequest.getRequestUri().endsWith(".js")) {
            final String responseBody = getResponseBody(httpRequest);
            return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, ContentType.JAVASCRIPT,
                    responseBody);
        }
        return new HttpResponse(httpRequest.getProtocol(), HttpStatus.NOT_FOUND, ContentType.HTML, null);
    }

    private String getResponseBody(final HttpRequest httpRequest) throws IOException {
        final URL resourceURL = getClass().getClassLoader()
                .getResource("static" + httpRequest.getRequestUri());
        try {
            final File file = new File(Objects.requireNonNull(resourceURL).toURI());
            final Path path = file.getAbsoluteFile().toPath();
            return new String(Files.readAllBytes(path));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
