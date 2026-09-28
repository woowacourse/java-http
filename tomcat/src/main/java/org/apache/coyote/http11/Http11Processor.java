package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
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
        try (
                final var input = new BufferedInputStream(connection.getInputStream());
                final var outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequestParser.parse(input);
            HttpResponse response = new HttpResponse();
            Session session = SessionManager.getInstance().findSession(request);

            if (request.isMethod(HttpMethod.GET)) {
                handleGetRequest(session, request, response);
            } else if (request.isMethod(HttpMethod.POST)) {
                handlePostRequest(session, request, response);
            }

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleGetRequest(Session session, HttpRequest request, HttpResponse response) throws IOException {
        if (request.matchesPath("/")) {
            response.addHeader(
                    "Content-Type",
                    "text/html;charset=utf-8"
            );
            response.addBody(
                    "Hello world!".getBytes(StandardCharsets.UTF_8)
            );
            return;
        }

        String resourceName = request.path().substring(1);

        if (request.matchesPath("/login")) {
            if (session != null && session.getAttribute("user") != null) {
                response.addHeader("Location", "/index.html");
                return;
            }

            resourceName = "login.html";
        } else if (request.matchesPath("/register")) {
            resourceName = "register.html";
        }

        response.fromResource(resourceName);
    }

    private void handlePostRequest(Session session,
                                   HttpRequest request,
                                   HttpResponse response) throws IOException {

        if (request.matchesPath("/register")) {
            handleRegister(request);
            response.addHeader("Location", "/index.html");
            return;
        }

        if (request.matchesPath("/login")) {
            Optional<User> authenticatedUser = authenticate(request);

            if (authenticatedUser.isPresent()) {
                if (session == null) {
                    session = SessionManager.getInstance().createSession(response);
                }

                session.setAttribute("user", authenticatedUser.get());
                response.addHeader("Location", "/index.html");
                return;
            }

            response.fromResource("401.html");
        }
    }

    private void handleRegister(HttpRequest request) {
        String account = request.getBodyValue("account");
        String email = request.getBodyValue("email");
        String password = request.getBodyValue("password");

        if (account == null || email == null || password == null) {
            return;
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private static Optional<User> authenticate(HttpRequest request) {
        String account = request.getBodyValue("account");
        String password = request.getBodyValue("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private void writeResponse(
            OutputStream outputStream,
            HttpResponse response
    ) throws IOException {
        byte[] responseBody = response.getBody();

        StringBuilder responseHead = new StringBuilder()
                .append("HTTP/1.1")
                .append(' ')
                .append(response.getStatus().getCode())
                .append(' ')
                .append(response.getStatus().getReasonPhrase())
                .append("\r\n");

        for (Map.Entry<String, String> header
                : response.getHeaders().entrySet()) {
            responseHead.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        responseHead.append("\r\n");

        outputStream.write(
                responseHead.toString()
                        .getBytes(StandardCharsets.UTF_8)
        );
        outputStream.write(responseBody);
        outputStream.flush();
    }
}
