package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final ContentTypeExtractor contentTypeExtractor = new ContentTypeExtractor();
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

            final HttpRequest httpRequest = createHttpRequest(inputStream);
            final HttpResponse response = createResponseBy(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(final InputStream inputStream) throws IOException {
        final List<String> lines = readRequestLines(inputStream);
        final String httpRequest = String.join("\r\n", lines);
        return HttpRequest.from(httpRequest);
    }

    private List<String> readRequestLines(final InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> lines = new ArrayList<>();

        String line = reader.readLine();
        while (!line.equals("")) {
            lines.add(line);
            line = reader.readLine();
        }
        return lines;
    }

    private HttpResponse createResponseBy(HttpRequest httpRequest) throws IOException {
        final String contentType = contentTypeExtractor.extract(httpRequest);

        if (httpRequest.getUri().equals("/")) {
            final HttpResponse response = HttpResponse.ok();
            response.addHeader("Content-Type", contentType);
            response.addBody("Hello world!");
            return response;
        }

        if (httpRequest.getUri().startsWith("/login")) {
            String account = httpRequest.getRequestParam("account");

            if (account != null) {
                final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);

                if (userOptional.isPresent()) {
                    final User user = userOptional.get();

                    if (user.checkPassword(httpRequest.getRequestParam("password"))) {
                        final HttpResponse response = HttpResponse.redirect();
                        response.addHeader("Location", "/index.html");
                        return response;
                    }
                }

                final HttpResponse response = HttpResponse.redirect();
                response.addHeader("Location", "/401.html");
                return response;
            }

            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final HttpResponse response = HttpResponse.ok();
            response.addHeader("Content-Type", contentType);
            response.addBody(responseBody);
            return response;
        }

        final URL resource = getClass().getClassLoader().getResource("static" + httpRequest.getUri());
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final HttpResponse response = HttpResponse.ok();
        response.addHeader("Content-Type", contentType);
        response.addBody(responseBody);
        return response;
    }
}
