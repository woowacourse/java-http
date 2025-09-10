package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8 ";
    private static final String TEXT_CSS_CHARSET_UTF_8 = "text/css;charset=utf-8 ";

    private final Socket connection;
    private String sessionId;

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader br = new BufferedReader(inputStreamReader);

            final List<String> headerLines = new ArrayList<>();
            String line;
            int contentLength = 0;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                headerLines.add(line);
                if (line.startsWith("Content-Length")) {
                    contentLength = getContentLengthBy(line);
                }
                if (line.startsWith("Cookie")) {
                    String cookieValue = line.split(":")[1];
                    sessionId = cookieValue.split("=")[1];
                }
            }

            final String startLine = headerLines.get(0);
            final String[] startLineParts = startLine.split(" ");
            final String httpMethod = startLineParts[0];
            final String endPoint = startLineParts[1];
            final String requestBody = getRequestBody(contentLength, br);

            if (httpMethod.equals("GET") && endPoint.equals("/")) {
                final String response = createResponse("Hello world!", TEXT_HTML_CHARSET_UTF_8);
                writeAndFlush(outputStream, response);
                return;
            }

            if (httpMethod.equals("GET") && endPoint.equals("/css/styles.css")) {
                final URL resource = getClass().getClassLoader().getResource("static" + endPoint);
                validateNullResource(resource);
                final String responseBody = Files.readString(Paths.get(resource.toURI()));
                final String response = createResponse(responseBody, TEXT_CSS_CHARSET_UTF_8);
                writeAndFlush(outputStream, response);
                return;
            }

            if (httpMethod.equals("GET") && endPoint.equals("/login")) {
                final Session session = SessionManager.find(sessionId);
                if (session != null) {
                    final String response = createRedirectionResponse("/index.html");
                    writeAndFlush(outputStream, response);
                    return;
                }

                final URL resource = getClass().getClassLoader().getResource("static" + endPoint + ".html");
                validateNullResource(resource);
                final String responseBody = Files.readString(Paths.get(resource.toURI()));
                final String response = createResponse(responseBody, TEXT_HTML_CHARSET_UTF_8);
                writeAndFlush(outputStream, response);
                return;
            }

            if (httpMethod.equals("POST") && endPoint.equals("/login")) {
                final String[] queryStringParts = requestBody.split("&");
                final String account = queryStringParts[0].split("=")[1];
                final String password = queryStringParts[1].split("=")[1];

                final User user = getUserByAccount(account);
                if (isLoginFailed(user, password)) {
                    unAuthenticationResponse(outputStream);
                    return;
                }

                log.info("user: {}", user);
                final URL resource = getClass().getClassLoader().getResource("static" + endPoint + ".html");
                validateNullResource(resource);
                final Cookie cookie = HttpCookie.createCookie();
                Session session = new Session(cookie.getValue());
                session.setAttribute("user", user);

                SessionManager.add(session);
                final String response = createRedirectionResponse("/index.html", cookie);
                writeAndFlush(outputStream, response);
                return;
            }

            if (httpMethod.equals("GET") && endPoint.equals("/register")) {
                final URL resource = getClass().getClassLoader().getResource("static" + endPoint + ".html");
                validateNullResource(resource);
                final String responseBody = Files.readString(Paths.get(resource.toURI()));
                final String response = createResponse(responseBody, TEXT_HTML_CHARSET_UTF_8);
                writeAndFlush(outputStream, response);
                return;
            }

            if (httpMethod.equals("POST") && endPoint.equals("/register")) {
                final String[] queryStringParts = requestBody.split("&");
                final String account = queryStringParts[0].split("=")[1];
                final String email = queryStringParts[1].split("=")[1];
                final String password = queryStringParts[2].split("=")[1];

                final User user = createUser(account, password, email);
                InMemoryUserRepository.save(user);
                final String response = createRedirectionResponse("/index.html");
                writeAndFlush(outputStream, response);
                return;
            }

            final URL resource = getClass().getClassLoader().getResource("static" + endPoint);
            validateNullResource(resource);
            final String responseBody = Files.readString(Paths.get(resource.toURI()));
            final String response = createResponse(responseBody, TEXT_HTML_CHARSET_UTF_8);
            writeAndFlush(outputStream, response);

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }


    private int getContentLengthBy(String line) {
        return Integer.parseInt(line.split(":")[1].trim());
    }

    private String getRequestBody(int contentLength, BufferedReader br) throws IOException {
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private User getUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElse(null);
    }

    private boolean isLoginFailed(final User user, final String password) {
        return user == null || isNotMatchPassword(user, password);
    }

    private boolean isNotMatchPassword(final User user, final String password) {
        return !user.checkPassword(password);
    }

    private void unAuthenticationResponse(final OutputStream outputStream) throws IOException, URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource("static" + "/401" + ".html");
        validateNullResource(resource);
        final String responseBody = Files.readString(Paths.get(resource.toURI()));
        final String response = createResponse(responseBody, TEXT_HTML_CHARSET_UTF_8);
        writeAndFlush(outputStream, response);
    }

    private void validateNullResource(final URL resource) {
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 resource 입니다.");
        }
    }

    private String createResponse(final String responseBody, final String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createRedirectionResponse(final String location, final Cookie cookie) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Set-Cookie: " + cookie.getName() + "=" + cookie.getValue(),
                "Location: " + location,
                "Content-Type: " + TEXT_HTML_CHARSET_UTF_8);
    }

    private String createRedirectionResponse(final String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location,
                "Content-Type: " + TEXT_HTML_CHARSET_UTF_8);
    }

    private User createUser(String account, String password, String email) {
        Long id = 1L;
        return new User(++id, account, password, email);
    }

    private void writeAndFlush(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private static class Session {

        private final String id;
        private final Map<String, Object> values = new HashMap<>();

        public Session(final String id) {
            this.id = id;
        }

        public void setAttribute(final String name, final Object value) {
            values.put(name, value);
        }
    }

    private static class SessionManager {

        private static final Map<String, Session> SESSIONS = new HashMap<>();

        public static void add(final Session session) {
            SESSIONS.put(session.id, session);
        }

        public static Session find(final String sessionId) {
            return SESSIONS.get(sessionId);
        }
    }
}

