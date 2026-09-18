package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int ELEMENT_SIZE = 2;

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
            OutputStream outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            final var requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final String[] tokens = requestLine.split(" ");
            final String method = tokens[0];
            final String path = tokens[1];
            int contentLength = 0;
            String cookieHeader = "";

            log.debug("request line: {}", requestLine);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.equals("\r") || line.equals("\n")) {
                    break;
                }

                if (line.startsWith("Content-Length: ")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }

                if (line.startsWith("Cookie: ")) {
                    cookieHeader = line.split(":")[1].trim();
                }
                log.debug("header : {}", line);
            }

            HttpCookie cookies = new HttpCookie(cookieHeader);

            if (getSessionId(cookies) == null) {
                cookies.add("JSESSIONID", UUID.randomUUID().toString());
            }

            char[] buffer = new char[contentLength];
            int bytesRead = reader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer, 0, bytesRead);

            if (path.equals("/")) {
                respondHelloWorld(outputStream);
                return;
            }
            if (path.equals("/register")) {
                handleRegister(cookies, requestBody, outputStream);
            }
            if (path.equals("/login") && method.equals("GET")) {
                handleLoginPage(cookies, outputStream);
                return;
            }
            if (path.equals("/login") && method.equals("POST")) {
                handleLogin(cookies, requestBody, outputStream);
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
                    "Set-Cookie " + cookies.sessionConcatenate() + " ",
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

    private void handleLoginPage(HttpCookie cookies, OutputStream outputStream) {
        String sessionId = getSessionId(cookies);

        Session session = SessionManager.getSession(sessionId);

        boolean loggedIn = session.getAttribute("user") != null;

        if (loggedIn) {
            respondStaticResource(cookies, "/index.html", outputStream);
            return;
        }

        respondStaticResource(cookies, "/login.html", outputStream);
    }

    private void handleLogin(HttpCookie cookies, String queryString, OutputStream outputStream) throws IOException {
        Map<String, String> params = parseQueryString(queryString);

        if (params.size() < 2) {
            log.debug("로그인 파라미터가 부족합니다.");
            return;
        }
        String account = params.get("account");
        String password = params.get("password");

        final User user = InMemoryUserRepository.findByAccount(account).get();

        if (user.checkPassword(password)) {
            final var session = SessionManager.getSession(getSessionId(cookies));
            session.setAttribute("user", user);
            log.debug("로그인 성공: {}", account);
            response302LoginSuccessHeader(cookies, outputStream);
        } else {
            log.debug("비밀번호 불일치: {}", account);
            respondStaticResource(cookies,"/401.html", outputStream);
        }
    }

    private void handleRegister(HttpCookie cookies, String queryString, OutputStream outputStream) {
        Map<String, String> params = parseQueryString(queryString);
        if (params.size() < 3) {
            log.debug("회원가입 파라미터가 부족합니다.");
            return;
        }
        User user = new User(params.get("account"), params.get("password"), params.get("email"));
        InMemoryUserRepository.save(user);
        log.debug("User : {}", user);
        response201UserCreatedHeader(outputStream);
        response302LoginSuccessHeader(cookies, outputStream);
    }

    private void response201UserCreatedHeader(OutputStream outputStream) {
        try {
            final var response = String.join("\r\n",
                    "HTTP/1.1 302 Redirect ",
                    "Location: /index.html ",
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

    private Map<String, String> parseQueryString(String queryString) {
        String[] queryList = queryString.split("&");
        Map<String, String> params = new HashMap<>();
        for (String str : queryList) {
            String[] query = str.split("=");
            if (query.length < ELEMENT_SIZE) {
                continue;
            }
            params.put(query[0], query[1]);
        }

        return params;
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

    private String getSessionId(HttpCookie cookies) {
        return cookies.getCookie("JSESSIONID");
    }
}
