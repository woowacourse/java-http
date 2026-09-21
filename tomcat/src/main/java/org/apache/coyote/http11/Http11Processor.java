package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String STATIC_RESOURCE_ROOT = "static/";
    private static final String CONTENT_TYPE_HTML = "text/html;charset=utf-8";
    private static final String CONTENT_TYPE_CSS = "text/css;charset=utf-8";
    private static final String CONTENT_TYPE_JS = "text/javascript;charset=utf-8";

    private static final String INDEX_PATH = "/index.html";
    private static final String CSS_PATH = "/css/styles.css";
    private static final String JOIN_PATH = "/register";
    private static final String LOGIN_PATH = "/login";

    private static final String UNAUTHORIZED_FILE = "401.html";

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

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String requestLine = bufferedReader.readLine();

            if (requestLine == null) {
                return;
            }

            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String uri = requestParts[1];
            String path = uri;

            int index = uri.indexOf("?");

            if (index != -1) {
                path = uri.substring(0, index);
            }

            int contentLength = 0;
            String cookieHeader = "";
            String line = bufferedReader.readLine();

            while (!"".equals(line)) {
                if (line == null) {
                    return;
                }

                String[] header = line.split(":", 2);

                if (header.length == 2 && header[0].equalsIgnoreCase("Content-Length")) {
                    contentLength = Integer.parseInt(header[1].trim());
                }

                if (header.length == 2 && header[0].equalsIgnoreCase("Cookie")) {
                    cookieHeader = header[1].trim();
                }

                line = bufferedReader.readLine();
            }

            Map<String, Cookie> cookies = parseCookies(cookieHeader);
            Cookie sessionCookie = cookies.get("JSESSIONID");
            String setCookieHeader = "";

            SessionManager sessionManager = SessionManager.getInstance();
            Session session = null;

            if (sessionCookie != null) {
                session = sessionManager.findSession(sessionCookie.getValue());
            }

            if (session == null) {
                String sessionId = UUID.randomUUID().toString();

                session = new Session(sessionId);
                sessionManager.add(session);

                sessionCookie = new Cookie("JSESSIONID", sessionId);

                setCookieHeader = "Set-Cookie: "
                        + sessionCookie.getName() + "=" + sessionCookie.getValue()
                        + "; Path=/\r\n";
            }

            var responseBody = "Hello world!";
            var contentType = CONTENT_TYPE_HTML;

            if (path.equals(INDEX_PATH)) {
                responseBody = readStaticFile(path.substring(1));
            }

            if (path.equals(CSS_PATH)) {
                responseBody = readStaticFile(path.substring(1));
                contentType = CONTENT_TYPE_CSS;
            }

            if (path.equals("/js/scripts.js")
                    || path.equals("/assets/chart-area.js")
                    || path.equals("/assets/chart-bar.js")
                    || path.equals("/assets/chart-pie.js")) {

                responseBody = readStaticFile(path.substring(1));
                contentType = CONTENT_TYPE_JS;
            }

            if (method.equals("GET") && path.equals(JOIN_PATH)) {
                responseBody = readStaticFile("register.html");
            }

            if (method.equals("POST") && path.equals(JOIN_PATH)) {
                String requestBody = readRequestBody(bufferedReader, contentLength);

                Map<String, String> parameters = parseFormData(requestBody);

                String account = parameters.getOrDefault("account", "");
                String email = parameters.getOrDefault("email", "");
                String password = parameters.getOrDefault("password", "");

                User newUser = new User(account, password, email);
                InMemoryUserRepository.save(newUser);

                String response = createRedirectResponse(INDEX_PATH, setCookieHeader);

                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            if (method.equals("GET") && path.equals(LOGIN_PATH)) {
                User loginUser = (User) session.getAttribute("user");

                if (loginUser != null) {
                    String response = createRedirectResponse(INDEX_PATH, setCookieHeader);

                    outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    return;
                }

                responseBody = readStaticFile("login.html");
            }

            if (method.equals("POST") && path.equals(LOGIN_PATH)) {
                String requestBody = readRequestBody(bufferedReader, contentLength);

                if (!requestBody.isEmpty()) {
                    Map<String, String> parameters = parseFormData(requestBody);

                    String account = parameters.getOrDefault("account", "");
                    String password = parameters.getOrDefault("password", "");

                    var user = InMemoryUserRepository.findByAccount(account);

                    if (user.isPresent()) {
                        if (user.get().checkPassword(password)) {
                            session.setAttribute("user", user.get());

                            log.info("로그인 성공 : account={}", user.get().getAccount());

                            String response = createRedirectResponse(INDEX_PATH, setCookieHeader);

                            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                            outputStream.flush();
                            return;

                        }

                        if (!user.get().checkPassword(password)) {
                            responseBody = readStaticFile(UNAUTHORIZED_FILE);
                        }
                    }
                }
            }

            final var response = createResponse(contentType, responseBody, setCookieHeader);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestBody(BufferedReader reader, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int count = reader.read(
                    body, totalRead, contentLength - totalRead
            );

            if (count == -1) {
                throw new IOException("요청 본문이 끝까지 도착하지 않았습니다.");
            }

            totalRead += count;
        }

        return new String(body);
    }

    // 입력 예시: "account=gugu&password=1234"
    private Map<String, String> parseFormData(String queryString) {
        Map<String, String> parameters = new HashMap<>();

        for (String parameter : queryString.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length != 2) {
                continue;
            }

            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

            parameters.put(key, value);

        }

        return parameters;
    }

    // 입력 예시: "yummy_cookie=choco; JSESSIONID=abc123"
    private Map<String,Cookie> parseCookies(String cookieHeader) {
        Map<String, Cookie> cookies = new HashMap<>();

        for (String part : cookieHeader.split(";")) {
            String[] nameValue = part.trim().split("=", 2);

            if (nameValue.length != 2) {
                continue;
            }

            String name = nameValue[0].trim();
            String value = nameValue[1].trim();

            cookies.put(name, new Cookie(name, value));
        }

        return cookies;
    }

    private String readStaticFile(String fileName) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader()
                .getResource(STATIC_RESOURCE_ROOT + fileName);

        Path filePath = Path.of(resource.toURI());
        return Files.readString(filePath, StandardCharsets.UTF_8);
    }

    private String createResponse(
            String contentType, String responseBody, String setCookieHeader
    ) {
        return "HTTP/1.1 200 OK\r\n"
                + setCookieHeader
                + String.join("\r\n",
                        "Content-Type: " + contentType,
                        "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length,
                        "",
                        responseBody);
    }

    private String createRedirectResponse(String location, String setCookieHeader) {
        return "HTTP/1.1 302 Found\r\n"
                + setCookieHeader
                + String.join("\r\n",
                "Location: " + location,
                "Content-Length: 0",
                "",
                "");
    }

}
