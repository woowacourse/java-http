package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.session.HttpCookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final Pattern REQUEST_LINE_PATTERN =
            Pattern.compile("^(?<method>[A-Z]+) (?<uri>\\S+) (?<version>HTTP/\\d\\.\\d)$");

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection, final SessionManager sessionManager) {
        this.connection = connection;
        this.sessionManager = sessionManager;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            // Request Line
            String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            Matcher matcher = REQUEST_LINE_PATTERN.matcher(requestLine);
            if (!matcher.matches()) {
                return;
            }

            // Request Header
            Map<String, String> headers = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] header = line.split(":", 2);
                headers.put(header[0].trim(), header[1].trim());
            }
            HttpCookie httpCookie = new HttpCookie(headers.get("Cookie"));
            Session session = sessionManager.findSession(httpCookie.getAttribute("JSESSIONID"));

            // Request Body
            String body = "";
            String contentLength = headers.get("Content-Length");
            if (contentLength != null) {
                char[] buffer = new char[Integer.parseInt(contentLength)];
                reader.read(buffer, 0, buffer.length);
                body = new String(buffer);
            }

            // THINK: 추후 uri -> path, queryParams 부분을 VO로 포장하여 응집. - P0
            URI uri = URI.create(matcher.group("uri"));
            String method = matcher.group("method");
            String path = uri.getPath();

            if (method.equals("GET") && path.equals("/register")) {
                path = "/register.html";
            }

            if (method.equals("POST") && path.equals("/register")) {
                Map<String, String> requestBody = parseQuery(body);
                String account = requestBody.get("account");
                String password = requestBody.get("password");
                String email = requestBody.get("email");

                User user = new User(account, password, email);
                InMemoryUserRepository.save(user);

                String responseBody = redirect("302 FOUND", "/index.html");
                outputStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            // THINK 반복되는 엔드포인트 매핑 리팩터링 - P1
            if (method.equals("GET") && path.equals("/login")) {
                log.info("로그인 GET 요청");
                if (session != null && session.getAttribute("user") != null) {
                    log.info("로그인 세션 확인 됨.");
                    String responseBody = redirect("302 FOUND", "/index.html");
                    outputStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    return;
                }
                path = "/login.html";
            }

            if (method.equals("POST") && path.equals("/login")) {
                Map<String, String> requestBody = parseQuery(body);
                String account = requestBody.get("account");
                String password = requestBody.get("password");
                Optional<User> loginedUser = InMemoryUserRepository.findByAccount(account)
                        .filter(user -> user.checkPassword(password));

                if (loginedUser.isPresent()) {
                    UUID sessionId = UUID.randomUUID();
                    HttpCookie cookie = new HttpCookie("JSESSIONID=" + sessionId);
                    Session newSession = new Session(sessionId.toString());
                    newSession.setAttribute("user", loginedUser.get());
                    sessionManager.add(newSession);

                    String responseBody = redirect("302 FOUND", "/index.html", cookie);
                    outputStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    return;
                } else {
                    String responseBody = redirect("302 FOUND", "/401.html");
                    outputStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    return;
                }
            }

            if (path.equals("/")) {
                path = "/index.html";
            }

            URL resourcePath = getClass().getClassLoader().getResource("static" + path);
            if (resourcePath == null) {
                final var notFoundResponse = getResponseBody("404 Not Found", "text/html", "<h1>404 Not Found</h1>");
                outputStream.write(notFoundResponse.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            final var contentType = contentTypeOf(path);
            final var content = Files.readString(Path.of(resourcePath.toURI()), StandardCharsets.UTF_8);
            final var responseBody = getResponseBody("200 OK", contentType, content);
            outputStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            log.error("유효하지 않은 URI : {}", e.getMessage(), e);
        }
    }

    // THINK QueryParam VO로 포장 - P0
    private Map<String, String> parseQuery(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=", 2);
            String key = URLDecoder.decode(entry[0], StandardCharsets.UTF_8);
            String value = entry.length > 1 ? URLDecoder.decode(entry[1], StandardCharsets.UTF_8) : "";
            result.put(key, value);
        }
        return result;
    }

    // THINK: 추후 응답 관련 내용을 응집화한 HttpResponse으로 포장 - P0
    public String getResponseBody(String status, String contentType, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: " + contentType + ";charset=utf-8",
                "Content-Length: " + bytes.length,
                "",
                content);
    }

    public String redirect(String status, String location) {
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Location: " + location,
                "Content-Length: 0",
                "");
    }

    public String redirect(String status, String location, HttpCookie cookie) {
        log.info("Cookie : {}", cookie.serialize());
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Location: " + location,
                "Set-Cookie: " + cookie.serialize(),
                "Content-Length: 0",
                "");
    }

    private String contentTypeOf(String path) {
        int dotIndex = path.lastIndexOf('.');
        String extension = (dotIndex == -1) ? "" : path.substring(dotIndex);

        return switch (extension) {
            case ".css" -> "text/css";
            case ".js" -> "text/javascript";
            case ".ico" -> "image/x-icon";
            default -> "text/html";
        };
    }

}
