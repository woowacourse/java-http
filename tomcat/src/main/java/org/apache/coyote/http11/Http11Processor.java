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
            HttpResponse response = new HttpResponse(outputStream);
            String method = request.getMethod();
            String path = request.getPath();

            if (request.getCookies().getCookie("JSESSIONID") == null) {
                response.addHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
            }

            if (path.equals("/")) {
                respondHelloWorld(outputStream);
                return;
            }
            if (path.equals("/register") && method.equals("POST")) {
                handleRegister(request, response);
                return;
            }
            if (path.equals("/login") && method.equals("GET")) {
                handleLoginPage(request, response);
                return;
            }
            if (path.equals("/login") && method.equals("POST")) {
                handleLogin(request, response);
                return;
            }
            response.forward(htmlParser(path));
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

    private String htmlParser(String path) {
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private void handleLoginPage(HttpRequest request, HttpResponse response) {
        Session session = request.getSession();

        boolean loggedIn = session.getAttribute("user") != null;
        if (loggedIn) {
            response.sendRedirect("/index.html");
            return;
        }
        response.forward("/login.html");
    }

    private void handleLogin(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getParamSize() < 2) {
            log.debug("로그인 파라미터가 부족합니다.");
            response.forward("/login.html");
            return;
        }
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        user -> {
                            if (!user.checkPassword(password)) {
                                log.debug("비밀번호 불일치: {}", account);
                                response.sendRedirect("/401.html");
                                return;
                            }

                            Session session = request.getSession();
                            session.setAttribute("user", user);
                            log.debug("로그인 성공: {}", account);
                            response.sendRedirect("/index.html");
                        },
                        () -> {
                            log.debug("존재하지 않는 계정: {}", account);
                            response.sendRedirect("/401.html");
                        }
                );
    }

    private void handleRegister(HttpRequest request, HttpResponse response) {
        if (request.getParamSize() < 3) {
            log.debug("회원가입 파라미터가 부족합니다.");
            response.forward("/register.html");
            return;
        }
        User user = new User(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        );
        InMemoryUserRepository.save(user);
        log.debug("User : {}", user);
        response.sendRedirect("/index.html");
    }
}
