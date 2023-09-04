package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            final HttpRequest request = HttpRequest.from(bufferedReader);

            final HttpResponse response = handleRequest(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    public HttpResponse handleRequest(final HttpRequest request) throws URISyntaxException {

        final String uriPath = request.getPath();

        if (uriPath.equals("/login")) {
            final String path = "/login.html";
            if (request.containsQuery()) {
                final boolean isAuthenticated = processLogin(request);
                final String redirectUrlPath = isAuthenticated ? "/index.html" : "/401.html";
                return HttpResponse.of(HttpStatus.FOUND, redirectUrlPath);
            }
            return HttpResponse.of(HttpStatus.OK, path);
        }

        return HttpResponse.of(HttpStatus.OK, uriPath);
    }

    public boolean processLogin(final HttpRequest request) {
        if (!request.containsQuery(ACCOUNT_KEY) || !request.containsQuery(PASSWORD_KEY)) {
            return false;
        }
        final String account = request.getQueryParameter(ACCOUNT_KEY);
        final String password = request.getQueryParameter(PASSWORD_KEY);
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.checkPassword(password)) {
                log.info(user.toString());
                return true;
            }
        }
        return false;
    }

}
