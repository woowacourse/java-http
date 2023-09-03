package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
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

            String responseBody = "Hello world!";
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            if (httpRequest.method().equals("POST")) {
                if (httpRequest.uri().equals("/register")) {
                    register(httpRequest, outputStream);
                    return;
                }

                if (httpRequest.uri().equals("/login")) {
                    Optional<String> jsessionid = httpRequest.getCookie("JSESSIONID");

                    if (jsessionid.isPresent()) {
                        loginWithSession(outputStream, jsessionid.get());
                        return;
                    }
                    login(httpRequest, outputStream);
                    return;
                }
            }

            if (!httpRequest.uri().equals("/")) {
                URL resource = getClass().getClassLoader().getResource("static" + httpRequest.uri());
                if (httpRequest.uri().equals("/login")) {
                    Optional<String> jsessionid = httpRequest.getCookie("JSESSIONID");
                    if (jsessionid.isPresent()) {
                        loginWithSession(outputStream, jsessionid.get());
                        return;
                    }
                    resource = getClass().getClassLoader().getResource("static" + httpRequest.uri() + ".html");
                }
                if (httpRequest.uri().equals("/register")) {
                    resource = getClass().getClassLoader().getResource("static" + httpRequest.uri() + ".html");
                }
                responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            }

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
            Optional<String> acceptHeader = httpRequest.getHeader("Accept");
            if (acceptHeader.isPresent() && acceptHeader.get().contains("text/css")) {
                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void register(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        String account = httpRequest.getBody("account")
                .orElseThrow(() -> new IllegalArgumentException("가입하기 위해선 아이디가 필요합니다"));
        String password = httpRequest.getBody("password")
                .orElseThrow(() -> new IllegalArgumentException("가입하기 위해선 비밀번호가 필요합니다"));
        String email = httpRequest.getBody("email")
                .orElseThrow(() -> new IllegalArgumentException("가입하기 위해선 이메일이 필요합니다"));
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다. 다른 아이디로 가입해주세요");
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        String response = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /index.html ",
                "",
                "");
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void loginWithSession(OutputStream outputStream, String jsessionId) throws IOException {
        SessionManager sessionManager = new SessionManager();
        Session session = sessionManager.findSession(jsessionId);
        if (session == null) {
            throw new IllegalArgumentException("잘못된 세션 아이디입니다");
        }
        Object user = session.getAttribute("user");
        if (user == null) {
            throw new IllegalArgumentException("잘못된 세션입니다");
        }
        String response = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /index.html ",
                "",
                "");
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void login(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        String account = httpRequest.getBody("account")
                .orElseThrow(() -> new IllegalArgumentException("로그인하기 위해선 아이디가 필요합니다"));
        String password = httpRequest.getBody("password")
                .orElseThrow(() -> new IllegalArgumentException("로그인하기 위해선 비밀번호가 필요합니다"));
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            if (user.get().checkPassword(password)) {
                UUID jsessionId = UUID.randomUUID();
                log.info(user.get().toString());
                Session session = new Session(jsessionId.toString());
                session.setAttribute("user", user.get());
                SessionManager sessionManager = new SessionManager();
                sessionManager.add(session);
                String response = String.join("\r\n",
                        "HTTP/1.1 302 FOUND ",
                        "Location: /index.html ",
                        "Set-Cookie: JSESSIONID=" + jsessionId + " ",
                        "",
                        "");
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
        }
        URL resource = getClass().getClassLoader().getResource("static/401.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String response = String.join("\r\n",
                "HTTP/1.1 401 UNAUTHORIZED ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
