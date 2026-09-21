package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER_ATTRIBUTE = "user";

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
            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String requestLine = reader.readLine();

            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            final String[] requestLineTokens = requestLine.split(" ");
            final String method = requestLineTokens[0];
            final String requestTarget = requestLineTokens[1];
            final String path = extractPath(requestTarget);
            final Map<String, String> headers = readHeaders(reader);
            final String requestBody = readBody(reader, headers);
            final Cookie cookie = Cookie.from(headers.get("cookie"));

            log.debug("{} {} 요청을 받았습니다. 본문 길이: {}", method, requestTarget, requestBody.length());

            if (method.equals("POST") && path.equals("/login")) {
                login(parseQueryString(requestBody), outputStream);
                return;
            }

            if (method.equals("POST") && path.equals("/register")) {
                register(parseQueryString(requestBody), outputStream);
                return;
            }

            if (path.equals("/")) {
                final var responseBody = "Hello world!";
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            String filePath = "static" + path;
            String contentType = "text/html;charset=utf-8";

            if (path.equals("/login")) {
                if (isLoggedIn(cookie)) {
                    log.info("이미 로그인된 사용자입니다. index.html로 이동합니다.");
                    sendRedirect(outputStream, INDEX_PAGE, null);
                    return;
                }
                filePath = "static/login.html";
            }

            if (path.equals("/register")) {
                filePath = "static/register.html";
            }

            if (path.endsWith(".css")) {
                contentType = "text/css;charset=utf-8";
            }

            if (path.endsWith(".js")) {
                contentType = "application/javascript;charset=utf-8";
            }


            final URL resource = getClass().getClassLoader().getResource(filePath);
            if (resource == null) {
                final var response404 = "HTTP/1.1 404 Not Found\r\n\r\n";
                outputStream.write(response404.getBytes());
                outputStream.flush();
                return;
            }

            final byte[] body = Files.readAllBytes(Path.of(resource.toURI()));

            final StringBuilder responseHeader = new StringBuilder()
                    .append("HTTP/1.1 200 OK ").append("\r\n")
                    .append("Content-Type: ").append(contentType).append(" ").append("\r\n")
                    .append("Content-Length: ").append(body.length).append(" ").append("\r\n");

            if (path.equals("/login") && !cookie.hasJSessionId()) {
                final Session session = createSession();
                responseHeader.append("Set-Cookie: ").append(JSESSIONID).append("=").append(session.getId())
                        .append(" ").append("\r\n");
            }
            responseHeader.append("\r\n");

            outputStream.write(responseHeader.toString().getBytes());
            outputStream.write(body);
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(final Map<String, String> params, final OutputStream outputStream) throws IOException {
        final String account = params.get("account");
        final String email = params.get("email");
        final String password = params.get("password");

        if (account == null || email == null || password == null) {
            log.info("회원가입에 필요한 정보가 입력되지 않았습니다.");
            sendRedirect(outputStream, UNAUTHORIZED_PAGE, null);
            return;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        log.info("회원가입이 완료되었습니다. account: {}", account);
        sendRedirect(outputStream, INDEX_PAGE, null);
    }

    private void sendRedirect(final OutputStream outputStream, final String location, final String sessionId)
            throws IOException {
        final StringBuilder response = new StringBuilder()
                .append("HTTP/1.1 302 Found ").append("\r\n")
                .append("Location: ").append(location).append(" ").append("\r\n");

        if (sessionId != null) {
            response.append("Set-Cookie: ").append(JSESSIONID).append("=").append(sessionId).append(" ").append("\r\n");
        }
        response.append("\r\n");

        outputStream.write(response.toString().getBytes());
        outputStream.flush();
    }

    private boolean isLoggedIn(final Cookie cookie) {
        final Session session = SessionManager.getInstance().findSession(cookie.getJSessionId());
        return session != null && session.getAttribute(USER_ATTRIBUTE) != null;
    }

    private Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        SessionManager.getInstance().add(session);
        return session;
    }


    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();

        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            final int separatorIndex = line.indexOf(":");
            if (separatorIndex != -1) {
                final String name = line.substring(0, separatorIndex).trim().toLowerCase();
                final String value = line.substring(separatorIndex + 1).trim();
                headers.put(name, value);
            }
            line = reader.readLine();
        }
        return headers;
    }

    private String readBody(final BufferedReader reader, final Map<String, String> headers) throws IOException {
        final String contentLength = headers.get("content-length");
        if (contentLength == null) {
            return "";
        }

        final int length = Integer.parseInt(contentLength);
        final char[] buffer = new char[length];
        final int readCount = reader.read(buffer, 0, length);
        if (readCount == -1) {
            return "";
        }
        return new String(buffer, 0, readCount);
    }

    private void login(final Map<String, String> params, final OutputStream outputStream) throws IOException {
        final String account = params.get("account");
        final String password = params.get("password");

        if (account == null || password == null) {
            log.info("아이디 또는 비밀번호가 입력되지 않았습니다.");
            sendRedirect(outputStream, UNAUTHORIZED_PAGE, null);
            return;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));

        if (user.isEmpty()) {
            log.info("아이디 또는 비밀번호가 일치하지 않습니다. account: {}", account);
            sendRedirect(outputStream, UNAUTHORIZED_PAGE, null);
            return;
        }

        log.info("로그인 성공! 아이디 : {}", account);

        final Session session = createSession();
        session.setAttribute(USER_ATTRIBUTE, user.get());
        sendRedirect(outputStream, INDEX_PAGE, session.getId());
    }

    private String extractPath(final String requestTarget) {
        final int queryIndex = requestTarget.indexOf("?");
        if (queryIndex == -1) {
            return requestTarget;
        }
        return requestTarget.substring(0, queryIndex);
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isBlank()) {
            return params;
        }

        final String[] pairs = queryString.split("&");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            if (keyValue[0].isBlank()) {
                continue;
            }
            if (keyValue.length == 2) {
                params.put(decode(keyValue[0]), decode(keyValue[1]));
            } else {
                params.put(decode(keyValue[0]), "");
            }
        }
        return params;
    }

    private String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
