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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.manager.Session;
import org.apache.catalina.manager.SessionManager;
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
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            SessionManager sessionManager = new SessionManager();

            String requestLine = bufferedReader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            Map<String, String> headers = new HashMap<>();
            String headerLine = bufferedReader.readLine();
            while (!headerLine.isBlank()) {
                String[] header = headerLine.split(": ");
                headers.put(header[0], header[1]);
                headerLine = bufferedReader.readLine();
            }

            StringBuilder body = new StringBuilder();
            int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));

            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                bufferedReader.read(bodyChars, 0, contentLength);
                body.append(bodyChars);
            }

            if (!requestLine.isBlank()) {
                String[] requestParts = requestLine.split(" ");
                String method = requestParts[0];
                String uri = requestParts[1];
                String version = requestParts[2];

                String responseBody = "Hello world!";

                if (!"/".equals(uri)) {
                    String path = uri;

                    if (path.contains("?")) {
                        int index = uri.indexOf("?");
                        path = uri.substring(0, index);
                    }

                    if ("GET".equals(method) && "/login".equals(path)) {
                        String resourcePath = "static" + path + ".html";

                        Optional<URL> resource = Optional.ofNullable(
                                getClass().getClassLoader().getResource(resourcePath));

                        if (resource.isPresent()) {
                            Cookie cookie = new Cookie(headers.getOrDefault("Cookie", ""));
                            Optional<String> optionalSessionId = cookie.getJSessionId();

                            if (optionalSessionId.isPresent()) {
                                String sessionId = optionalSessionId.get();
                                Session session = sessionManager.findSession(sessionId);
                                if (session != null) {
                                    String response = String.join("\r\n",
                                            "HTTP/1.1 302 FOUND ",
                                            "Location: /index.html ",
                                            "Content-Length: 0 ",
                                            "");

                                    bufferedWriter.write(response);
                                    bufferedWriter.flush();
                                    return;
                                }
                            }

                            responseBody = new String(Files.readAllBytes(new File(resource.get().getFile()).toPath()));

                            final String response = String.join("\r\n",
                                    "HTTP/1.1 200 OK ",
                                    "Content-Type: " + ContentType.findWithCharset(uri) + " ",
                                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                                    "",
                                    responseBody);

                            bufferedWriter.write(response);
                            bufferedWriter.flush();
                            return;
                        }
                    }

                    if ("POST".equals(method) && "/login".equals(path)) {
                        Map<String, String> params = new HashMap<>();

                        String[] paramPairs = body.toString().split("&");
                        for (String pair : paramPairs) {
                            String[] keyValue = pair.split("=");
                            if (keyValue.length == 2) {
                                params.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
                            }
                        }

                        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(params.get("account"));

                        if (optionalUser.isPresent()) {
                            User user = optionalUser.get();
                            if (user.checkPassword(params.get("password"))) {
                                Cookie cookie = new Cookie(headers.getOrDefault("Cookie", ""));
                                Optional<String> sessionId = cookie.getJSessionId();

                                if (sessionId.isEmpty()) {
                                    UUID uuid = UUID.randomUUID();
                                    Session session = new Session(uuid.toString());
                                    session.setAttribute("user", user);
                                    sessionManager.add(session);
                                    cookie.addCookie(Cookie.JSESSIONID, uuid.toString());
                                }

                                String response = String.join("\r\n",
                                        "HTTP/1.1 302 FOUND",
                                        "Set-Cookie: " + cookie.toCookieHeader(),
                                        "Location: /index.html",
                                        "Content-Length: 0",
                                        "");

                                bufferedWriter.write(response);
                                bufferedWriter.flush();

                                log.info("로그인 성공! 아이디 : {}", user.getAccount());
                                return;
                            }
                        }

                        String response = String.join("\r\n",
                                "HTTP/1.1 302 FOUND ",
                                "Location: /401.html ",
                                "Content-Length: 0 ",
                                "");

                        bufferedWriter.write(response);
                        bufferedWriter.flush();
                        return;
                    }

                    if ("GET".equals(method) && "/register".equals(path)) {
                        String resourcePath = "static" + path + ".html";

                        Optional<URL> resource = Optional.ofNullable(
                                getClass().getClassLoader().getResource(resourcePath));

                        if (resource.isPresent()) {
                            responseBody = new String(Files.readAllBytes(new File(resource.get().getFile()).toPath()));

                            final String response = String.join("\r\n",
                                    "HTTP/1.1 200 OK ",
                                    "Content-Type: " + ContentType.findWithCharset(uri) + " ",
                                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                                    "",
                                    responseBody);

                            bufferedWriter.write(response);
                            bufferedWriter.flush();
                            return;
                        }
                    }

                    if ("POST".equals(method) && "/register".equals(path)) {
                        Map<String, String> params = new HashMap<>();

                        String[] paramPairs = body.toString().split("&");
                        for (String pair : paramPairs) {
                            String[] keyValue = pair.split("=");
                            if (keyValue.length == 2) {
                                params.put(
                                        URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                                );
                            }
                        }

                        String account = params.get("account");
                        String password = params.get("password");
                        String email = params.get("email");
                        User newUser = new User(account, password, email);

                        InMemoryUserRepository.save(newUser);

                        String response = String.join("\r\n",
                                "HTTP/1.1 302 FOUND ",
                                "Location: /index.html ",
                                "Content-Length: 0 ",
                                "");

                        bufferedWriter.write(response);
                        bufferedWriter.flush();
                        return;
                    }
                }

                String resourcePath = "static" + uri;
                Optional<URL> resource = Optional.ofNullable(getClass().getClassLoader().getResource(resourcePath));

                if (resource.isPresent()) {
                    responseBody = new String(Files.readAllBytes(new File(resource.get().getFile()).toPath()));
                }

                final String response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + ContentType.findWithCharset(uri) + " ",
                        "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                        "",
                        responseBody);

                bufferedWriter.write(response);
                bufferedWriter.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
