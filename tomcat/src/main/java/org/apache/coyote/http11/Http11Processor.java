package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.BadRequestException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.coyote.Processor;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            HttpResponse response = new HttpResponse(outputStream);
            try {
                HttpRequest request = new HttpRequest(inputStream);
                String path = request.getPath();
                dispatchRequest(request, response);
            } catch (BadRequestException e) {
                response.sendBadRequest();
            } catch (Exception e) {
                response.sendInternalServerError();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void dispatchRequest(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();

        if ("/".equals(path)) {
            handleRoot(response);
        } else if ("/login".equals(path)) {
            handleLogin(request, response);
        } else if ("/register".equals(path)) {
            handleRegister(request, response);
        } else {
            handleStaticResource(response, path);
        }
    }

    private void handleRegister(HttpRequest request, HttpResponse response) throws IOException {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String account = request.getQueryParam("account");
            String password = request.getQueryParam("password");
            String email = request.getQueryParam("email");

            if (account != null && password != null && email != null) {
                User user = new User(account, password, email);
                InMemoryUserRepository.save(user);
                log.info("회원가입 완료: {}", user);
                response.sendRedirect("/index.html");
                return;
            }
        }

        loadStaticResource(response, "/register.html");
    }

    private void handleRoot(HttpResponse response) throws IOException {
        final var responseBody = "Hello world!";
        response.sendOk("text/html;charset=utf-8", responseBody.getBytes(StandardCharsets.UTF_8));
    }

    private void handleLogin(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getQueryParam("account");
        String password = request.getQueryParam("password");

        if (account != null && password != null) {
            Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
            if (userOptional.isPresent() && userOptional.get().checkPassword(password)) {
                log.info("로그인 성공: {}", userOptional.get());
                response.sendRedirect("index.html");
            } else {
                log.info("로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.");
                response.sendRedirect("/401.html");

            }
        }

        loadStaticResource(response, "/login.html");
    }

    private void handleStaticResource(HttpResponse response, String path) throws IOException {
        loadStaticResource(response, path);
    }

    private void loadStaticResource(HttpResponse response, String path) throws IOException {
        String resourcePath = "static" + path;
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (fileInputStream == null) {
                response.sendNotFound();
                return;
            }

            String contentType = determineContentType(path);
            byte[] bodyBytes = fileInputStream.readAllBytes();
            response.sendOk(contentType, bodyBytes);
        }
    }

    private String determineContentType(String path) {
        return ContentType.fromPath(path);
    }
}
