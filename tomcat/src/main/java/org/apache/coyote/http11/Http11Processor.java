package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;
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
        try (InputStream inputStream = connection.getInputStream();
            OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest request = new HttpRequest(inputStream);
            HttpCookie cookies = request.getCookies();
            String method = request.getMethod();
            String path = request.getPath();

            if (cookies.getCookie("JSESSIONID") == null) {
                cookies.add("JSESSIONID", UUID.randomUUID().toString());
            }

            if (path.equals("/")) {
                respondHelloWorld(outputStream);
                return;
            }
            if (path.equals("/register") && method.equals("POST")) {
                handleRegister(cookies, request, outputStream);
                return;
            }
            if (path.equals("/login") && method.equals("GET")) {
                handleLoginPage(cookies, request, outputStream);
                return;
            }
            if (path.equals("/login") && method.equals("POST")) {
                handleLogin(cookies, request, outputStream);
                return;
            }
            respondStaticResource(cookies, htmlParser(path), outputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void respondHelloWorld(OutputStream outputStream) {
        try {
            final var responseBody = "Hello world!";

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void respondStaticResource(HttpCookie cookies, String requestTarget, OutputStream outputStream) {
        try (final var resourceStream = getClass()
                .getClassLoader()
                .getResourceAsStream("static" + requestTarget)) {

            byte[] responseBody = new byte[0];
            if (resourceStream != null) {
                responseBody = resourceStream.readAllBytes();
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Set-Cookie: " + cookies.sessionConcatenate() + " ",
                    "Content-Type: " + contentType(requestTarget),
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String htmlParser(String path) {
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private void handleLoginPage(HttpCookie cookies, HttpRequest request, OutputStream outputStream) {
        String sessionId = getSessionId(request);
        Session session = SessionManager.getSession(sessionId);

        boolean loggedIn = session.getAttribute("user") != null;

        if (loggedIn) {
            respondStaticResource(cookies, "/index.html", outputStream);
            return;
        }

        respondStaticResource(cookies, "/login.html", outputStream);
    }

    private void handleLogin(HttpCookie cookies, HttpRequest request, OutputStream outputStream) throws IOException {
        if (request.getParamSize() < 2) {
            log.debug("로그인 파라미터가 부족합니다.");
            respondStaticResource(cookies, "/login.html", outputStream);
            return;
        }
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        user -> {
                            if (!user.checkPassword(password)) {
                                log.debug("비밀번호 불일치: {}", account);
                                respondStaticResource(cookies, "/401.html", outputStream);
                                return;
                            }

                            Session session = SessionManager.getSession(getSessionId(request));

                            session.setAttribute("user", user);

                            log.debug("로그인 성공: {}", account);
                            response302LoginSuccessHeader(cookies, outputStream);
                        },
                        () -> {
                            log.debug("존재하지 않는 계정: {}", account);
                            respondStaticResource(cookies, "/401.html", outputStream);
                        }
                );
    }

    private void handleRegister(HttpCookie cookies, HttpRequest request, OutputStream outputStream) {
        if (request.getParamSize() < 3) {
            log.debug("회원가입 파라미터가 부족합니다.");
            respondStaticResource(cookies, "/register.html", outputStream);
            return;
        }
        User user = new User(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        );
        InMemoryUserRepository.save(user);
        log.debug("User : {}", user);
        response201UserCreatedHeader(cookies, outputStream);
        respondStaticResource(cookies, "/index.html", outputStream);
    }

    private void response201UserCreatedHeader(HttpCookie cookies, OutputStream outputStream) {
        try {
            final var response = String.join("\r\n",
                    "HTTP/1.1 201 Created ",
                    "Set-Cookie: " + cookies.sessionConcatenate() + " ",
                    "Content-Type: application/json ",
                    "");

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void response302LoginSuccessHeader(HttpCookie cookies, OutputStream outputStream) {
        try {
            final var response = String.join("\r\n",
                    "HTTP/1.1 302 Redirect ",
                    "Set-Cookie: " + cookies.sessionConcatenate() + " ",
                    "Location: /index.html ",
                    "");

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String contentType(final String requestTarget) {
        if (requestTarget.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (requestTarget.endsWith(".js")) {
            return "application/javascript;charset=utf-8 ";
        }
        if (requestTarget.endsWith(".svg")) {
            return "image/svg+xml ";
        }
        return "text/html;charset=utf-8 ";
    }

    private String getSessionId(HttpRequest request) {
        return request.getCookies().getCookie("JSESSIONID");
    }
}
