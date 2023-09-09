package org.apache.coyote.http11;


import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.ContentType;
import org.apache.coyote.Processor;
import org.apache.coyote.request.*;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.apache.coyote.ContentType.HTML;

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            HttpRequest httpRequest = generateHttpRequest(reader);

            RequestUri requestUri = httpRequest.getRequestLine().getRequestUri();

            String path = requestUri.getPath();

            System.out.println("path = " + path);

            HttpResponse response = null;

            // / 일때 분기
            if (path.equals("/")) {
                response = new HttpResponse.Builder()
                        .contentType(HTML)
                        .body("hello world")
                        .build();
            } else {
                final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static" + requestUri.getPath())).getPath());
                var responseBody = new String(Files.readAllBytes(filePath));

                response = new HttpResponse.Builder()
                        .contentType(ContentType.from(requestUri.getExtension()))
                        .body(responseBody)
                        .build();
            }
//
//            // path 가 login 일때 분기
//            if (path.equals("/login") && httpRequestLine.isGetMethod()) {
//                statusCode = 302;
//                statusMessage = "Found";
//
//                HttpCookie cookie = httpRequestHeader.getCookie();
//
//                if (cookie == null || !cookie.existJSESSIONID()) {
//                    path = "/login.html";
//                } else {
//                    String jSessionId = cookie.getJSessionId();
//                    Session session = SessionManager.findSession(jSessionId);
//
//                    Optional<User> user = (Optional<User>) session.getAttribute("user");
//
//                    if (user.isPresent()) {
//                        path = "/index.html";
//                    } else {
//                        path = "/login.html";
//                    }
//                }
//
//                final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static" + path)).getPath());
//                responseBody = new String(Files.readAllBytes(filePath));
//            }
//
//
//            if (path.equals("/login") && httpRequestLine.isPostMethod()) {
//                statusCode = 302;
//                statusMessage = "Found";
//
//                String account = httpRequestBody.getValue("account");
//                String password = httpRequestBody.getValue("password");
//
//                Optional<User> user = InMemoryUserRepository.findByAccount(account);
//                if (user.isPresent() && user.get().checkPassword(password)) {
//                    log.info(user.toString());
//                    UUID uuid = UUID.randomUUID();
//                    Session session = new Session(uuid.toString());
//                    session.setAttribute("user", user);
//                    SessionManager.add(session);
//
//                    setCookie = "Set-Cookie: JSESSIONID=" + uuid;
//
//                    path = "/index.html";
//                } else if (user.isPresent()) {
//                    log.warn("비밀번호가 틀렸습니다");
//
//                    path = "/401.html";
//
//                } else {
//                    log.warn("미가입회원입니다");
//
//                    path = "/401.html";
//                }
//                final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static" + path)).getPath());
//
//                responseBody = new String(Files.readAllBytes(filePath));
//            }
//
//            // pass 가 register 일때 분기
//            if (path.equals("/register") && httpRequestLine.isPostMethod()) {
//                String account = httpRequestBody.getValue("account");
//                String password = httpRequestBody.getValue("password");
//                String email = httpRequestBody.getValue("email");
//
//                InMemoryUserRepository.save(new User(account, password, email));
//
//                statusCode = 302;
//                statusMessage = "Found";
//                path = "/index.html";
//                final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static" + path)).getPath());
//
//                responseBody = new String(Files.readAllBytes(filePath));
//            }

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();

        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest generateHttpRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();

        if (requestLine == null) {
            return null;
        }

        HttpRequestLine httpRequestLine = HttpRequestLine.from(requestLine);
        HttpRequestHeader httpRequestHeader = getRequestHeader(reader);

        int contentLength = httpRequestHeader.getContentLength();
        HttpRequestBody httpRequestBody = getRequestBody(reader, contentLength);

        return new HttpRequest(httpRequestLine, httpRequestHeader, httpRequestBody);
    }

    private HttpRequestBody getRequestBody(BufferedReader reader, int contentLength) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            int bytesRead = reader.read(buffer, 0, contentLength);
            requestBody.append(buffer, 0, bytesRead);
        }

        return HttpRequestBody.from(requestBody.toString());
    }

    private HttpRequestHeader getRequestHeader(BufferedReader reader) throws IOException {
        StringBuilder requestHeader = new StringBuilder();

        String line;
        while (!Objects.equals(line = reader.readLine(), "")) {
            requestHeader.append(line).append("\r\n");
        }

        return HttpRequestHeader.from(requestHeader.toString());
    }
}
