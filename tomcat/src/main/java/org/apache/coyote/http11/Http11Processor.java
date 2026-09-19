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
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
            HttpResponse response = new HttpResponse(outputStream);
            if (request.getCookies().getValue("JSESSIONID").isEmpty()) {
                response.addCookie("JSESSIONID", UUID.randomUUID().toString());
            }

            if (request.getMethod().equals("POST") && request.getPath().equals("/register")) {
                register(request.getBodyParams());
                response.sendRedirect("/index.html");
                return;
            }

            if (request.getMethod().equals("POST") && request.getPath().equals("/login")) {
                final boolean loginSucceed = login(request.getBodyParams());
                final String location = loginSucceed ? "/index.html" : "/401.html";
                response.sendRedirect(location);
                return;
            }

            String statusLine = "200 OK";
            byte[] responseBody = createResponseBody(request.getPath());
            if (responseBody == null) {
                statusLine = "404 Not Found";
                responseBody = createResponseBody("/404.html");
            }
            String contentType = determineContentType(request.getPath());

            response.setStatus(statusLine);
            response.addHeader("Content-Type", contentType);
            response.setBody(responseBody);
            response.send();
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
