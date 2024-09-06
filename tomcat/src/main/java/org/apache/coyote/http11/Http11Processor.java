package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.manager.Session;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.startLine.HttpMethod;
import org.apache.coyote.http11.response.startLine.HttpStatus;
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
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            SessionManager sessionManager = new SessionManager();

            HttpRequest httpRequest = HttpRequest.create(bufferedReader);
            String uri = httpRequest.getUri();

            if ("/".equals(uri)) {
                String responseBody = "Hello world!";
                final String response = String.join("\r\n",
                        "HTTP/1.1 " + HttpStatus.OK.compose(),
                        "Content-Type: " + ContentType.findWithCharset(uri) + " ",
                        "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                        "",
                        responseBody
                );

                bufferedWriter.write(response);
                bufferedWriter.flush();
                return;
            }

            String path = uri;
            if (path.contains("?")) {
                int index = uri.indexOf("?");
                path = uri.substring(0, index);
            }

            if (httpRequest.matchesMethod(HttpMethod.GET) && "/login".equals(path)) {
                String resourcePath = "static" + path + ".html";
                Optional<URL> resource = Optional.ofNullable(getClass().getClassLoader().getResource(resourcePath));

                if (resource.isPresent()) {
                    Optional<String> cookieHeader = httpRequest.getHeader("Cookie");
                    Cookie cookie = new Cookie(cookieHeader.orElse(""));

                    Optional<String> sessionId = cookie.getJSessionId();
                    if (sessionId.isPresent()) {
                        Optional<Session> session = sessionManager.findSession(sessionId.get());

                        if (session.isPresent()) {
                            String response = String.join("\r\n",
                                    "HTTP/1.1 " + HttpStatus.FOUND.compose(),
                                    "Location: /index.html ",
                                    "Content-Length: 0 ",
                                    ""
                            );

                            bufferedWriter.write(response);
                            bufferedWriter.flush();
                            return;
                        }
                    }

                    String responseBody = new String(Files.readAllBytes(new File(resource.get().getFile()).toPath()));

                    String response = String.join("\r\n",
                            "HTTP/1.1 " + HttpStatus.OK.compose(),
                            "Content-Type: " + ContentType.findWithCharset(uri) + " ",
                            "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                            "",
                            responseBody
                    );

                    bufferedWriter.write(response);
                    bufferedWriter.flush();
                    return;
                }
            }

            if (httpRequest.matchesMethod(HttpMethod.POST) && "/login".equals(path)) {
                RequestBody requestBody = httpRequest.getRequestBody();
                Optional<User> optionalUser = InMemoryUserRepository.findByAccount(requestBody.get("account"));

                if (optionalUser.isPresent() && optionalUser.get().checkPassword(requestBody.get("password"))) {
                    User user = optionalUser.get();
                    Optional<String> cookieHeader = httpRequest.getHeader("Cookie");
                    Cookie cookie = new Cookie(cookieHeader.orElse(""));

                    Optional<String> sessionId = cookie.getJSessionId();
                    if (sessionId.isEmpty()) {
                        UUID uuid = UUID.randomUUID();
                        Session session = new Session(uuid.toString());
                        session.setAttribute("user", user);
                        sessionManager.add(session);
                        cookie.addCookie(Cookie.JSESSIONID, uuid.toString());
                    }

                    String response = String.join("\r\n",
                            "HTTP/1.1 " + HttpStatus.FOUND.compose(),
                            "Set-Cookie: " + cookie.toCookieHeader(),
                            "Location: /index.html",
                            "Content-Length: 0",
                            ""
                    );

                    bufferedWriter.write(response);
                    bufferedWriter.flush();

                    log.info("로그인 성공! 아이디 : {}", user.getAccount());
                    return;
                }

                String response = String.join("\r\n",
                        "HTTP/1.1 " + HttpStatus.FOUND.compose(),
                        "Location: /401.html ",
                        "Content-Length: 0 ",
                        ""
                );

                bufferedWriter.write(response);
                bufferedWriter.flush();
                return;
            }

            if (httpRequest.matchesMethod(HttpMethod.GET) && "/register".equals(path)) {
                String resourcePath = "static" + path + ".html";

                Optional<URL> resource = Optional.ofNullable(getClass().getClassLoader().getResource(resourcePath));

                if (resource.isPresent()) {
                    String responseBody = new String(Files.readAllBytes(new File(resource.get().getFile()).toPath()));

                    String response = String.join("\r\n",
                            "HTTP/1.1 " + HttpStatus.FOUND.compose(),
                            "Content-Type: " + ContentType.findWithCharset(uri) + " ",
                            "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                            "",
                            responseBody
                    );

                    bufferedWriter.write(response);
                    bufferedWriter.flush();
                    return;
                }
            }

            if (httpRequest.matchesMethod(HttpMethod.POST) && "/register".equals(path)) {
                RequestBody requestBody = httpRequest.getRequestBody();

                String account = requestBody.get("account");
                String password = requestBody.get("password");
                String email = requestBody.get("email");
                User newUser = new User(account, password, email);

                InMemoryUserRepository.save(newUser);

                String response = String.join("\r\n",
                        "HTTP/1.1 " + HttpStatus.FOUND.compose(),
                        "Location: /index.html ",
                        "Content-Length: 0 ",
                        ""
                );

                bufferedWriter.write(response);
                bufferedWriter.flush();
                return;
            }

            String resourcePath = "static" + uri;
            Optional<URL> resource = Optional.ofNullable(getClass().getClassLoader().getResource(resourcePath));
            if (resource.isPresent()) {
                String responseBody = new String(Files.readAllBytes(new File(resource.get().getFile()).toPath()));
                String response = String.join("\r\n",
                        "HTTP/1.1 " + HttpStatus.OK.compose(),
                        "Content-Type: " + ContentType.findWithCharset(uri) + " ",
                        "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                        "",
                        responseBody
                );

                bufferedWriter.write(response);
                bufferedWriter.flush();
                return;
            }

            String response = String.join("\r\n",
                    "HTTP/1.1 " + HttpStatus.FOUND.compose(),
                    "Location: /404.html ",
                    "Content-Length: 0 ",
                    ""
            );

            bufferedWriter.write(response);
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
