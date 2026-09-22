package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final long DEFAULT_USER_ID = 999L;
    private static final String LINE_SEPARATOR = "\r\n";

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            HttpRequest httpRequest = readHttpRequest(inputStream);
            String response = buildResponse(httpRequest);
            response = addSessionCookie(httpRequest, response);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String addSessionCookie(HttpRequest httpRequest, String response) {
        if (httpRequest.getCookies().hasSessionId()) {
            return response;
        }

        String cookieHeader = "Set-Cookie: JSESSIONID=" + httpRequest.getOrCreateSession().getId() + LINE_SEPARATOR;
        int endOfStatusLine = response.indexOf(LINE_SEPARATOR) + LINE_SEPARATOR.length();
        return response.substring(0, endOfStatusLine)
                + cookieHeader
                + response.substring(endOfStatusLine);
    }

    private HttpRequest readHttpRequest(InputStream inputStream) throws IOException {
        StringBuilder request = new StringBuilder();
        int contentLength = 0;

        String line;
        while (!(line = readLine(inputStream)).isEmpty()) {
            request.append(line).append(LINE_SEPARATOR);

            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(
                        line.substring("Content-Length:".length()).trim()
                );
            }
        }

        request.append(LINE_SEPARATOR);

        byte[] body = inputStream.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new RuntimeException("본문을 모두 읽기 전에 연결이 종료되었습니다.");
        }
        request.append(new String(body, StandardCharsets.UTF_8));

        return new HttpRequest(request.toString());
    }

    private String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int value;
        while ((value = inputStream.read()) != -1 && value != '\n') {
            if (value != '\r') {
                line.write(value);
            }
        }
        return line.toString(StandardCharsets.US_ASCII);
    }

    private String buildResponse(HttpRequest httpRequest) {
        if ("/login".equals(httpRequest.getPath())) {
            return buildLoginResponse(httpRequest);
        }
        if ("/register".equals(httpRequest.getPath())) {
            return buildRegisterResponse(httpRequest);
        }
        if ("/".equals(httpRequest.getPath())) {
            return buildRootResponse();
        }

        return buildResourceResponse(httpRequest.getPath());
    }

    private String buildLoginResponse(HttpRequest httpRequest) {
        Session session = httpRequest.findSession();
        if ("GET".equals(httpRequest.getMethod()) && isLoggedIn(session)) {
            return String.join(LINE_SEPARATOR,
                    "HTTP/1.1 302 Found",
                    "Location: /index.html",
                    "",
                    "");
        }

        if (!"POST".equals(httpRequest.getMethod())) {
            return buildResourceResponse("/login.html");
        }

        String account = httpRequest.getBody().get("account");
        String password = httpRequest.getBody().get("password");
        var user = InMemoryUserRepository.findByAccountAndPassword(account, password);
        if (user.isPresent()) {
            httpRequest.getOrCreateSession().setAttribute("user", user.get());
            return String.join(LINE_SEPARATOR,
                    "HTTP/1.1 302 Found",
                    "Location: /index.html",
                    "",
                    "");
        }

        return String.join(LINE_SEPARATOR,
                "HTTP/1.1 302 Found",
                "Location: /401.html",
                "",
                "");
    }

    private boolean isLoggedIn(Session session) {
        return session != null && session.getAttribute("user") != null;
    }

    private String buildRegisterResponse(HttpRequest httpRequest) {
        if (!"POST".equals(httpRequest.getMethod())) {
            return buildResourceResponse("/register.html");
        }

        register(httpRequest.getBody());

        return String.join(LINE_SEPARATOR,
                "HTTP/1.1 302 Found",
                "Location: /index.html",
                "",
                "");
    }

    private void register(Map<String, String> requestBody) {
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");

        User user = new User(DEFAULT_USER_ID, account, password, email);
        InMemoryUserRepository.save(user);
    }

    private String buildRootResponse() {
        String responseBody = "Hello world!";

        return String.join(LINE_SEPARATOR,
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
    }

    private String buildResourceResponse(String path) {
        final var resource = findResource(path);
        if (resource == null) {
            throw new RuntimeException("자원을 찾을 수 없습니다.");
        }

        return String.join(LINE_SEPARATOR,
                "HTTP/1.1 200 OK ",
                "Content-Type: " + getContentType(path) + " ",
                "Content-Length: " + resource.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                resource);
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "application/octet-stream";
    }

    private String findResource(String path) {
        try (final var inputStream = Http11Processor.class
                .getClassLoader()
                .getResourceAsStream("static" + path)) {
            if (inputStream == null) {
                return null;
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
