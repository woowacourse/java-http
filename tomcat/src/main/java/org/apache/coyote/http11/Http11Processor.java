package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
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

            HttpRequest request = new HttpRequest(inputStream);
            log.info("[요청] - {}", request);
            HttpResponse response = getResponse(request);
            log.info("[응답] - {}", response);
            outputStream.write(response.convertMessage().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getResponse(HttpRequest request) throws IOException {
        if (request.isGetMethod()) {
            return doGet(request);
        }
        if (request.isPostMethod()) {
            return doPost(request);
        }
        return null;
    }

    private HttpResponse doGet(HttpRequest request) throws IOException {
        String path = request.getPath();

        if (path.endsWith(".css")) {
            URL resource = getClass().getClassLoader().getResource("static" + path);
            String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return new HttpResponse(
                    request.getProtocolVersion(),
                    HttpStatusCode.OK,
                    List.of("Content-Type: text/css;charset=utf-8"),
                    responseBody
            );
        }

        if (path.endsWith(".js")) {
            URL resource = getClass().getClassLoader().getResource("static" + path);
            String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return new HttpResponse(
                    request.getProtocolVersion(),
                    HttpStatusCode.OK,
                    List.of("Content-Type: text/javascript;charset=utf-8"),
                    responseBody
            );
        }

        if (path.endsWith(".svg")) {
            URL resource = getClass().getClassLoader().getResource("static" + path);
            String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return new HttpResponse(
                    request.getProtocolVersion(),
                    HttpStatusCode.OK,
                    List.of("Content-Type: image/svg+xml"),
                    responseBody
            );
        }

        if (path.equals("/login")) {
            String cookies = request.getCookies();
            HttpCookie httpCookie = new HttpCookie(cookies);
            if (httpCookie.hasKey("JSESSIONID")) {
                return new HttpResponse(
                        request.getProtocolVersion(),
                        HttpStatusCode.FOUND,
                        List.of("Location: http://localhost:8080/index.html", "Content-Type: text/html;charset=utf-8")
                );
            }
        }

        if (path.equals("/")) {
            String responseBody = "Hello world!";
            return new HttpResponse(
                    request.getProtocolVersion(),
                    HttpStatusCode.OK,
                    List.of("Content-Type: text/html;charset=utf-8"),
                    responseBody
            );
        }

        if (!path.endsWith(".html")) {
            path += ".html";
        }
        URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource != null) {
            String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return new HttpResponse(
                    request.getProtocolVersion(),
                    HttpStatusCode.OK,
                    List.of("Content-Type: text/html;charset=utf-8"),
                    responseBody
            );
        }
        return null;
    }

    private HttpResponse doPost(HttpRequest request) {
        String path = request.getPath();
        String requestBody = request.getBody();

        if (path.equals("/register")) {
            StringTokenizer tokenizer = new StringTokenizer(requestBody, "=|&");
            String account = "";
            String password = "";
            String email = "";
            while (tokenizer.hasMoreTokens()) {
                String key = tokenizer.nextToken();
                if (key.equals("account") && tokenizer.hasMoreTokens()) {
                    account = tokenizer.nextToken();
                } else if (key.equals("password") && tokenizer.hasMoreTokens()) {
                    password = tokenizer.nextToken();
                } else if (key.equals("email") && tokenizer.hasMoreTokens()) {
                    email = tokenizer.nextToken();
                }
            }
            if (account.isBlank() || password.isBlank() || email.isBlank()) { // TODO: 예외처리 개선
                throw new IllegalArgumentException("올바르지 않은 request body 형식입니다.");
            }
            InMemoryUserRepository.save(new User(account, password, email));
            return new HttpResponse(
                    request.getProtocolVersion(),
                    HttpStatusCode.FOUND,
                    List.of("Location: http://localhost:8080/index.html", "Content-Type: text/html;charset=utf-8")
            );
        }

        if (path.equals("/login")) {
            StringTokenizer tokenizer = new StringTokenizer(requestBody, "=|&");
            String account = "";
            String password = "";
            while (tokenizer.hasMoreTokens()) {
                String key = tokenizer.nextToken();
                if (key.equals("account") && tokenizer.hasMoreTokens()) {
                    account = tokenizer.nextToken();
                } else if (key.equals("password") && tokenizer.hasMoreTokens()) {
                    password = tokenizer.nextToken();
                }
            }

            Optional<User> loginUser = InMemoryUserRepository.findByAccount(account);
            if (loginUser.isPresent()) {
                final User user = loginUser.get();
                if (user.checkPassword(password)) {
                    log.info("로그인 성공! 아이디: {}", user.getAccount());

                    String cookies = request.getCookies();
                    HttpCookie httpCookie = new HttpCookie(cookies);
                    if (httpCookie.hasKey("JSESSIONID")) {
                        return new HttpResponse(
                                request.getProtocolVersion(),
                                HttpStatusCode.FOUND,
                                List.of(
                                        "Location: http://localhost:8080/index.html",
                                        "Content-Type: text/html;charset=utf-8"
                                )
                        );
                    }
                    Cookie cookie = CookieManager.setCookie();
                    Session session = new Session(cookie.getValue());
                    session.setAttribute("user", user);
                    SessionManager.getInstance().add(session);
                    return new HttpResponse(
                            request.getProtocolVersion(),
                            HttpStatusCode.FOUND,
                            List.of(
                                    "Location: http://localhost:8080/index.html",
                                    "Set-Cookie: " + cookie.getKeyValue(),
                                    "Content-Type: text/html;charset=utf-88"
                            )
                    );
                }
            }
            return new HttpResponse(
                    request.getProtocolVersion(),
                    HttpStatusCode.FOUND,
                    List.of(
                            "Location: http://localhost:8080/401.html",
                            "Content-Type: text/html;charset=utf-88"
                    )
            );
        }
        return null;
    }
}
