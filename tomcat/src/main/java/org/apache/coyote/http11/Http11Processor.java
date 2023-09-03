package org.apache.coyote.http11;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

import java.io.IOException;
import java.net.Socket;

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
            String request = getRequest(bufferedReader);
            String[] split = request.split(" ");
            String method = split[0];
            String requestUri = split[1];


            String[] split1 = request.split("\r\n");

            if (method.equals("POST")) {
                Optional<String> any = Arrays.stream(split1)
                        .filter(it -> it.contains("Content-Length"))
                        .findAny();
                if (any.isEmpty()) {
                    throw new IllegalArgumentException("Content-Length 헤더가 존재하지 않습니다");
                }
                String s = any.get();
                int index = s.indexOf(":");
                int contentLength = Integer.parseInt(s.substring(index + 2));
                String body = getBody(bufferedReader, contentLength);

                String[] split2 = body.split("&");
                Map<String, String> form = new HashMap<>();
                for (String line : split2) {
                    String[] split3 = line.split("=");
                    String key = split3[0];
                    String value = split3[1];
                    form.put(key, value);
                }

                if (requestUri.equals("/register")) {
                    register(form, outputStream);
                    return;
                }

                if (requestUri.equals("/login")) {
                    Optional<String> cookie = Arrays.stream(split1)
                            .filter(it -> it.contains("Cookie"))
                            .findAny();
                    if (cookie.isPresent()) {
                        String a = cookie.get();
                        String[] split100 = a.split(":");
                        String cookies = split100[1];
                        String[] split101 = cookies.split(";");
                        Optional<String> any2 = Arrays.stream(split101)
                                .filter(it -> it.contains("JSESSIONID"))
                                .findAny();
                        if (any2.isPresent()) {
                            String[] split3 = any2.get().split("=");
                            String jsessionId = split3[1];
                            loginWithSession(outputStream, jsessionId);
                            return;
                        }
                    }
                    login(form, outputStream);
                    return;
                }
            }

            if (!requestUri.equals("/")) {
                URL resource = getClass().getClassLoader().getResource("static" + requestUri);
                if (requestUri.equals("/login")) {
                    Optional<String> cookie = Arrays.stream(split1)
                            .filter(it -> it.contains("Cookie"))
                            .findAny();
                    if (cookie.isPresent()) {
                        String s = cookie.get();
                        String[] split100 = s.split(":");
                        String cookies = split100[1];
                        String[] split101 = cookies.split(";");
                        Optional<String> any = Arrays.stream(split101)
                                .filter(it -> it.contains("JSESSIONID"))
                                .findAny();
                        if (any.isPresent()) {
                            String[] split2 = any.get().split("=");
                            String jsessionId = split2[1];
                            loginWithSession(outputStream, jsessionId);
                            return;
                        }
                    }
                    resource = getClass().getClassLoader().getResource("static" + requestUri + ".html");
                }
                if (requestUri.equals("/register")) {
                    resource = getClass().getClassLoader().getResource("static" + requestUri + ".html");
                }
                responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            }

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            if (request.contains("Accept: text/css")) {
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

    private void register(Map<String, String> form, OutputStream outputStream) throws IOException {
        Optional<User> account = InMemoryUserRepository.findByAccount(form.get("account"));
        if (account.isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다. 다른 아이디로 가입해주세요");
        }
        User user = new User(form.get("account"), form.get("password"), form.get("email"));
        System.out.println(user);
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

    private void login(Map<String, String> form, OutputStream outputStream) throws IOException {
        Optional<User> user = InMemoryUserRepository.findByAccount(form.get("account"));
        if (user.isPresent()) {
            if (user.get().checkPassword(form.get("password"))) {
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

    private String getRequest(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (!"".equals(line)) {
            stringBuilder.append(line);
            stringBuilder.append("\r\n");
            line = bufferedReader.readLine();
        }
        return stringBuilder.toString();
    }

    private String getBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        System.out.println(contentLength);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
