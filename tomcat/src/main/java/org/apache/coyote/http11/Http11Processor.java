package org.apache.coyote.http11;


import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.ContentType;
import org.apache.coyote.HttpCookie;
import org.apache.coyote.Processor;
import org.apache.coyote.Session;
import org.apache.coyote.request.*;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static org.apache.coyote.ContentType.HTML;
import static org.apache.coyote.response.HttpStatus.FOUND;
import static org.apache.coyote.response.HttpStatus.OK;

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            HttpRequest httpRequest = generateHttpRequest(reader);

            RequestUri requestUri = httpRequest.getRequestLine().getRequestUri();

            String path = requestUri.getPath();

            HttpResponse response = null;

            if (path.equals("/")) {
                response = new HttpResponse.Builder()
                        .contentType(HTML)
                        .body("Hello world!")
                        .build();
            } else if (path.equals("/login") && httpRequest.getRequestLine().isGetMethod()) {
                response = getLoginHttpResponse(httpRequest);
            } else if (path.equals("/login") && httpRequest.getRequestLine().isPostMethod()) {
                response = postLoginHttpResponse(httpRequest);
            } else if (path.equals("/register") && httpRequest.getRequestLine().isGetMethod()) {
                final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static/register.html")).getPath());
                var responseBody = new String(Files.readAllBytes(filePath));

                response = new HttpResponse.Builder()
                        .contentType(ContentType.from(requestUri.getExtension()))
                        .body(responseBody)
                        .build();
            } else if (path.equals("/register") && httpRequest.getRequestLine().isPostMethod()) {
                HttpRequestBody httpRequestBody = httpRequest.getRequestBody();

                String account = httpRequestBody.getValue("account");
                String password = httpRequestBody.getValue("password");
                String email = httpRequestBody.getValue("email");

                InMemoryUserRepository.save(new User(account, password, email));

                final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static/index.html")).getPath());

                response = new HttpResponse.Builder()
                        .status(FOUND)
                        .contentType(HTML)
                        .header("Location", "/index.html")
                        .build();
            } else {
                final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static" + requestUri.getPath())).getPath());
                var responseBody = new String(Files.readAllBytes(filePath));

                response = new HttpResponse.Builder()
                        .contentType(ContentType.from(requestUri.getExtension()))
                        .body(responseBody)
                        .build();
            }

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();

        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest generateHttpRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();

        if (requestLine == null) {
            return null;
        }

        HttpRequestLine httpRequestLine = HttpRequestLine.from(requestLine);
        HttpRequestHeader httpRequestHeader = getRequestHeader(reader);

        int contentLength = httpRequestHeader.getContentLength();
        HttpRequestBody httpRequestBody = getRequestBody(reader, contentLength);

        return new HttpRequest(httpRequestLine, httpRequestHeader, httpRequestBody);
    }

    private HttpRequestBody getRequestBody(BufferedReader reader, int contentLength) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            int bytesRead = reader.read(buffer, 0, contentLength);
            requestBody.append(buffer, 0, bytesRead);
        }

        return HttpRequestBody.from(requestBody.toString());
    }

    private HttpRequestHeader getRequestHeader(BufferedReader reader) throws IOException {
        StringBuilder requestHeader = new StringBuilder();

        String line;
        while (!Objects.equals(line = reader.readLine(), "")) {
            requestHeader.append(line).append("\r\n");
        }

        return HttpRequestHeader.from(requestHeader.toString());
    }

    private HttpResponse getLoginHttpResponse(HttpRequest httpRequest) throws IOException {
        HttpCookie cookie = httpRequest.getRequestHeader().getCookie();

        String jsessionid = cookie.getValue("JSESSIONID");
        Session session = SessionManager.findSession(jsessionid);

        if (session != null) {
            final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static/index.html")).getPath());
            return new HttpResponse.Builder()
                    .status(FOUND)
                    .header("Location", "/index.html")
                    .contentType(HTML)
                    .body(new String(Files.readAllBytes(filePath)))
                    .build();
        }


        final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static/login.html")).getPath());
        return new HttpResponse.Builder()
                .status(OK)
                .contentType(HTML)
                .body(new String(Files.readAllBytes(filePath)))
                .build();
    }

    private HttpResponse postLoginHttpResponse(HttpRequest httpRequest) {
        HttpRequestBody httpRequestBody = httpRequest.getRequestBody();
        String account = httpRequestBody.getValue("account");
        String password = httpRequestBody.getValue("password");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info(user.toString());

            return loginSuccess(user);

        } else if (user.isPresent()) {
            log.warn("비밀번호가 틀렸습니다");

            return loginFail();

        } else {
            log.warn("미가입회원입니다");

            return loginFail();
        }
    }

    private HttpResponse loginSuccess(Optional<User> user) {
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);

        HttpCookie cookie = HttpCookie.of("JSESSIONID=" + session.getId());

        final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static/index.html")).getPath());

        return new HttpResponse.Builder()
                .status(FOUND)
                .contentType(HTML)
                .header("Location", "/index.html")
                .setCookie(cookie)
                .build();
    }

    private HttpResponse loginFail() {
        final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static/401.html")).getPath());

        return new HttpResponse.Builder()
                .status(FOUND)
                .contentType(HTML)
                .header("Location", "/401.html")
                .build();
    }
}
