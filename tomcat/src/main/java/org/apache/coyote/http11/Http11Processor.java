package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Manager sessionManager = SessionManager.getInstance();
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

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = br.readLine();
            if (line == null) {
                return;
            }

            String[] str = line.split(" ", 3);
            String method = str[0];
            URI uri = URI.create(str[1]);
            String path = uri.getRawPath();

            Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                String[] header = line.split(":", 2);
                if (header.length == 2) {
                    headers.put(header[0].trim(), header[1].trim());
                }
            }

            HttpCookie cookie = new HttpCookie(headers.get("Cookie"));
            Session session = sessionManager.findSession(cookie.get("JSESSIONID"));
            String cookieHeader = "";
            if (session == null) {
                session = new Session();
                sessionManager.add(session);
                cookieHeader = "Set-Cookie: JSESSIONID=" + session.getId() + "; Path=/\r\n";
            }

            String responseBody = "Hello world!";
            String contentType = contentType(path);

            if (method.equals("POST")) {
                handlePost(path, br, outputStream, headers, session, cookieHeader);
                return;
            }

            if (method.equals("GET")) {
                handleGet(path, outputStream, session, cookieHeader);
                return;
            }

            sendOk(outputStream, contentType, responseBody, cookieHeader);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleGet(String path, OutputStream outputStream, Session session,
                           String cookieHeader) throws IOException {
        if (path.equals("/login") || path.equals("/login.html")) {
            if (session.getAttribute("user") != null) {
                sendRedirect(outputStream, "/index.html", cookieHeader);
                return;
            }
            path = "/login.html";
        }

        if (path.equals("/register")) {
            path = "/register.html";
        }

        String responseBody = "Hello world!";
        if (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js")) {
            responseBody = readFile("static" + path);
        }
        sendOk(outputStream, contentType(path), responseBody, cookieHeader);
    }

    private void handlePost(String path, BufferedReader reader, OutputStream outputStream,
                            Map<String, String> headers, Session session, String cookieHeader) throws IOException {
        if (!path.equals("/login") && !path.equals("/register")) {
            sendOk(outputStream, contentType(path), "Hello world!", cookieHeader);
            return;
        }

        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));

        char[] buffer = new char[contentLength];
        int offset = 0;
        while (offset < contentLength) {
            int count = reader.read(buffer, offset, contentLength - offset);
            if (count == -1) {
                throw new IOException("요청 본문을 모두 읽지 못했습니다.");
            }
            offset += count;
        }

        Map<String, String> formParams = new HashMap<>();
        for (String parameter : new String(buffer).split("&")) {
            String[] pair = parameter.split("=", 2);
            if (pair.length == 2) {
                formParams.put(
                        URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }

        String account = formParams.get("account");
        String password = formParams.get("password");
        if (path.equals("/register")) {
            InMemoryUserRepository.save(new User(account, password, formParams.get("email")));
            sendRedirect(outputStream, "/index.html", cookieHeader);
            return;
        }

        if (account == null || password == null) {
            sendRedirect(outputStream, "/401.html", cookieHeader);
            return;
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password))
                .orElse(null);
        if (user == null) {
            sendRedirect(outputStream, "/401.html", cookieHeader);
            return;
        }

        session.setAttribute("user", user);
        log.info("회원 조회 성공: {}", user.getAccount());
        sendRedirect(outputStream, "/index.html", cookieHeader);
    }

    private void sendOk(OutputStream outputStream, String contentType, String responseBody,
                        String cookieHeader) throws IOException {
        String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                cookieHeader,
                responseBody);

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private void sendRedirect(OutputStream outputStream, String location, String cookieHeader) throws IOException {
        String response = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "Content-Length: 0",
                cookieHeader,
                "");

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String readFile(String fileName) throws IOException {
        try {
            var resource = getClass().getClassLoader().getResource(fileName);
            if (resource == null) {
                throw new FileNotFoundException("리소스를 찾을 수 없습니다: " + fileName);
            }

            Path path = Path.of(resource.toURI());
            return Files.readString(path, StandardCharsets.UTF_8);

        } catch (URISyntaxException e) {
            throw new IOException("리소스 경로 변환에 실패했습니다: " + fileName, e);
        }
    }

    private String contentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html";
        }

        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "text/javascript";
        }

        return "text/html";
    }
}
