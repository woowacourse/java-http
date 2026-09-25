package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String INDEX_PATH = "/index.html";
    private static final String LOCATION = "Location: ";
    private static final String CRLF = "\r\n";

    private final Socket connection;

    // 요청
    private String method;
    private String path;
    private String requestBody;
    private Optional<String> setCookieHeader;
    private final Map<String, String> queryParameters = new HashMap<>();
    private final Map<String, String> formParameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    // 응답
    private byte[] httpResponse;
    private final StringBuilder bodyBuilder = new StringBuilder();

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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8));
             final var outputStream = connection.getOutputStream()) {

            parseRequestLine(reader);
            parseHeaders(reader);

            Session session = findOrCreateSession();

            if (method.equals("GET")) {
                httpResponse = handleGetRequest(session);
                writeResponse(outputStream);
                return;
            }
            if (method.equals("POST")) {
                readRequestBody(reader);
                formParameters.putAll(parseParameters(requestBody));
                httpResponse = handlePostRequest(session);
                writeResponse(outputStream);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void parseRequestLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("EOF: No request line received");
        }

        String[] parts = line.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid line received: " + line);
        }

        method = parts[0];
        String[] pathParts = parts[1].split("\\?", 2);
        path = pathParts[0];

        if (pathParts.length > 1) {
            queryParameters.putAll(parseParameters(pathParts[1]));
        }
    }

    private void parseHeaders(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(":", 2);

            if (headerParts.length == 2) {
                String name = headerParts[0].trim().toLowerCase();
                String value = headerParts[1].trim();

                headers.put(name, value);
            }
        }
    }

    private Map<String, String> parseParameters(String queryString) {
        Map<String, String> parameters = new HashMap<>();
        for (String param : queryString.split("&")) {
            if (param.isEmpty()) {
                continue;
            }

            String[] keyValue = param.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], UTF_8);
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], UTF_8) : "";
            parameters.put(key, value);
        }

        return parameters;
    }

    private void readRequestBody(BufferedReader reader) throws IOException {
        String value = headers.get("content-length");
        if (value == null) {
            throw new IOException("Content-Length header is missing");
        }

        int length = Integer.parseInt(value);
        char[] body = new char[length];
        int offset = 0;

        while (offset < length) {
            int count = reader.read(body, offset, length - offset);
            if (count == -1) {
                throw new IOException("요청 바디가 Content-Length보다 짧습니다.");
            }
            offset += count;
        }

        requestBody = new String(body);
    }

    private byte[] handleGetRequest(Session session) throws IOException {
        if (path.equals("/login")) {
            User user = (User) session.getAttribute("user");

            if (user != null) {
                return createRedirectResponse(INDEX_PATH).getBytes(UTF_8);
            }

            return serveStaticFile(path + ".html");
        }
        if (path.equals("/register")) {
            return serveStaticFile(path + ".html");
        }
        if (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js")) {
            return serveStaticFile(path);
        }
        if (path.equals("/")) {
            return serverHomePage();
        }
        return notFound();
    }

    private byte[] notFound() {
        return createNotFoundResponse("/404.html").getBytes(UTF_8);
    }

    private byte[] handlePostRequest(Session session) {
        if (path.equals("/login")) {
            return loginResult(session);
        }
        if (path.equals("/register")) {
            return registerResult();
        }
        return notFound();
    }

    private byte[] loginResult(Session session) {
        String account = formParameters.get("account");
        String password = formParameters.get("password");

        Optional<User> authenticatedUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (authenticatedUser.isPresent()) {
            session.setAttribute("user", authenticatedUser.get());
            return createRedirectResponse(INDEX_PATH).getBytes(UTF_8);
        }

        return createNotFoundResponse("/401.html").getBytes(UTF_8);
    }

    private Session findOrCreateSession() throws IOException {
        HttpCookie cookie = HttpCookie.parse(headers.get("cookie"));
        SessionManager sessionManager = new SessionManager();

        Optional<String> sessionId = cookie.get("JSESSIONID");

        if (sessionId.isPresent()) {
            Session session = sessionManager.findSession(sessionId.get());

            if (session != null) {
                setCookieHeader = Optional.empty();
                return session;
            }
        }

        String newSessionId = UUID.randomUUID().toString();
        Session newSession = new Session(newSessionId);

        sessionManager.add(newSession);

        setCookieHeader = Optional.of("Set-Cookie: JSESSIONID=" + newSessionId);

        return newSession;
    }

    private byte[] registerResult() {
        String account = formParameters.get("account");
        String password = formParameters.get("password");
        String email = formParameters.get("email");

        InMemoryUserRepository.save(new User(account, password, email));

        return createRedirectResponse(INDEX_PATH).getBytes(UTF_8);
    }

    private byte[] serveStaticFile(String requestUri) throws IOException {
        String contentType = findContentType(requestUri);

        URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource("static" + requestUri));
        String body = Files.readString(Path.of(resource.getPath()), UTF_8);
        writeBody(body);

        return createOkHttpResponse(contentType).getBytes(UTF_8);
    }

    private byte[] serverHomePage() {
        writeBody("Hello world!");
        return createOkHttpResponse(findContentType("/")).getBytes(UTF_8);
    }

    private String findContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }

    private void writeBody(String body) {
        bodyBuilder.append(body);
    }

    @Nonnull
    private String createOkHttpResponse(String contentType) {
        String body = bodyBuilder.toString();

        return createHttpResponse(
                "200 OK",
                List.of(
                        "Content-Type: " + contentType,
                        "Content-Length: " + body.getBytes(UTF_8).length
                ),
                body
        );
    }

    private String createRedirectResponse(String location) {
        return createHttpResponse(
                "302 FOUND",
                List.of(
                        LOCATION + location + " ",
                        "Content-Length: 0 "
                ),
                ""
        );
    }

    private String createNotFoundResponse(String location) {
        return createHttpResponse(
                "404 NOT FOUND",
                List.of(
                        LOCATION + location + " ",
                        "Content-Length: 0 "
                ),
                ""
        );
    }

    private String createHttpResponse(String status, List<String> headers, String body) {
        List<String> lines = new ArrayList<>();
        lines.add("HTTP/1.1 " + status);
        setCookieHeader.ifPresent(lines::add);
        lines.addAll(headers);
        lines.add("");
        lines.add(body);

        return String.join(CRLF, lines);
    }

    private void writeResponse(OutputStream outputStream) throws IOException {
        outputStream.write(httpResponse);
        outputStream.flush();
    }
}
