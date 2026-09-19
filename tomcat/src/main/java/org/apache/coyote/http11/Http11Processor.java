package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest request = HttpRequest.from(reader);

            if (request.getMethod().equals("POST") && request.getPath().equals("/register")) {
                register(request.getBodyParams());
                sendRedirect(outputStream, "/index.html");
                return;
            }

            if (request.getPath().equals("/login") && !request.getQueryParams().isEmpty()) {
                final boolean loginSucceed = login(request.getQueryParams());
                final String location = loginSucceed ? "/index.html" : "/401.html";
                sendRedirect(outputStream, location);
                return;
            }

            String statusLine = "200 OK";
            byte[] responseBody = createResponseBody(request.getPath());
            if (responseBody == null) {
                statusLine = "404 Not Found";
                responseBody = createResponseBody("/404.html");
            }
            String contentType = determineContentType(request.getPath());

            final var response = String.join("\r\n",
                    "HTTP/1.1 " + statusLine + " ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void register(final Map<String, String> params) {
        final User user = new User(
                params.get("account"),
                params.get("password"),
                params.get("email")
        );
        InMemoryUserRepository.save(user);
    }

    private boolean login(final Map<String, String> params) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"));
        if (user.isEmpty()) {
            log.info("존재하지 않는 계정입니다.");
            return false;
        }

        final User foundUser = user.get();
        if (!foundUser.checkPassword(params.get("password"))) {
            log.info("비밀번호가 일치하지 않습니다.");
            return false;
        }

        log.info("로그인 성공! 아이디 : {}", foundUser.getAccount());
        return true;
    }

    private void sendRedirect(final OutputStream outputStream, final String location) throws IOException {
        final String response = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                "");

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private byte[] createResponseBody(final String path) throws IOException {
        if (path.equals("/")) {
            return "Hello world!".getBytes();
        }
        String resourcePath = path;
        if (path.equals("/login") || path.equals("/register")) {
            resourcePath = path + ".html";
        }
        final URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
        if (resource == null) {
            return null;
        }
        final File file = new File(resource.getFile());
        return Files.readAllBytes(file.toPath());
    }

    private String determineContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
