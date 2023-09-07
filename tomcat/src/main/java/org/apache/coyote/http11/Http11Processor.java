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
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";

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

        final HttpResponse response = new HttpResponse();

        final String uriPath = request.getPath();

        if (uriPath.equals("/login")) {
            if (request.getMethod().equals("GET")) {
                response.hostingPage(LOGIN_PAGE);
                return response;
            }
            if (request.getMethod().equals("POST")) {
                final boolean isAuthenticated = processLogin(request);
                if (isAuthenticated) {
                    final UUID sessionId = UUID.randomUUID();
                    response.setCookie(JSESSIONID_COOKIE_NAME, sessionId.toString());
                    response.redirectTo(INDEX_PAGE);
                    return response;
                }
                response.redirectTo(UNAUTHORIZED_PAGE);
                return response;
            }
        }

        if (uriPath.equals("/register")) {
            if (request.getMethod().equals("GET")) {
                response.hostingPage(REGISTER_PAGE);
                return response;
            }
            if (request.getMethod().equals("POST")) {
                processRegister(request);
                final UUID sessionId = UUID.randomUUID();
                response.redirectTo(INDEX_PAGE);
                response.setCookie(JSESSIONID_COOKIE_NAME, sessionId.toString());
                return response;
            }
        }

        response.hostingPage(uriPath);
        return response;
    }

    public boolean processLogin(final HttpRequest request) {
        if (!request.containsBody(ACCOUNT_KEY) || !request.containsBody(PASSWORD_KEY)) {
            return false;
        }
        final String account = request.getBody(ACCOUNT_KEY);
        final String password = request.getBody(PASSWORD_KEY);
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

    public User processRegister(final HttpRequest request) {
        if (!request.containsBody(ACCOUNT_KEY) || !request.containsBody(PASSWORD_KEY) || !request.containsBody(EMAIL_KEY)) {
            return null;
        }
        final String account = request.getBody(ACCOUNT_KEY);
        final String password = request.getBody(PASSWORD_KEY);
        final String email = request.getBody(EMAIL_KEY);

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return null;
        }
        final User registeredUser = new User(account, password, email);
        InMemoryUserRepository.save(registeredUser);
        return registeredUser;
    }
}
