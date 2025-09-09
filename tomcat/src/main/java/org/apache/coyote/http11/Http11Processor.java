package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.LoginParam;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String QUERY_PARAM = "?";

    private final Socket connection;
    private final HttpCookie httpCookie = new HttpCookie();
    private final SessionManager sessionManager = new SessionManager();

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
             final var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {

            final var request = getRequest(reader);

            String[] requestLineParts = getRequestLineParts(request);

            String method = requestLineParts[0];
            String requestPath = requestLineParts[1];

            Map<String, String> headers = parseHeaders(reader);
            if (headers.containsKey("Cookie")) {
                validateUserCookie(headers.get("Cookie"));
            }

            if (MethodType.isGetMethod(method)) {
                if (requestPath.equals("/")) {
                    final var response = getResponse();

                    sendResponse(outputStream, response);
                    return;
                }

                if (requestPath.startsWith("/login")) {
                    if (requestPath.contains(QUERY_PARAM)) {
                        authenticateUserFromRequestPath(requestPath, outputStream);
                        return;
                    }

                    serveStaticFile(requestPath, outputStream);
                    return;
                }

                serveStaticFile(requestPath, outputStream);
            }

            if (MethodType.isPostMethod(method)){
                int contentLength = toInt(headers.get("Content-Length"));

                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);

                registerUser(requestBody, outputStream);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void serveStaticFile(String requestPath, OutputStream outputStream) throws IOException {
        final var path = getPath(requestPath);
        if (Files.exists(path)) {
            final var responseHeaders = getResponse(path);
            sendResponse(outputStream, responseHeaders);

            try(InputStream inputStream = Files.newInputStream(path)) {
                inputStream.transferTo(outputStream);
            }
            return;
        }

        String notFoundResponse = getNotFoundResponse();
        sendResponse(outputStream, notFoundResponse);
    }

    private Path getPath(String requestPath) {
        if (requestPath.startsWith("/login")) {
            return Path.of(
                    Objects.requireNonNull(
                            getClass().getClassLoader().getResource("static/login.html")).getPath()
            );
        }

        if (requestPath.startsWith("/register")) {
            return Path.of(
                    Objects.requireNonNull(
                            getClass().getClassLoader().getResource("static/register.html")).getPath()
            );
        }

        var resource = getClass().getClassLoader().getResource("static/" + requestPath);
        return Path.of(Objects.requireNonNullElseGet(
                resource, () -> Objects.requireNonNull(
                        getClass().getClassLoader().getResource("static/404.html")
                )
        ).getPath());
    }

    private Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while((line = reader.readLine()) != null && !line.isEmpty()) {
            int index = line.indexOf(":");
            if (index > 0) {
                String key = line.substring(0, index).trim();
                String value = line.substring(index+1).trim();
                headers.put(key, value);
            }
        }

        return headers;
    }

    private String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );
    }

    private String getResponse(Path path) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + Files.probeContentType(path) + ";charset=utf-8 ",
                "Content-Length: " + Files.size(path) + " ",
                "", "");
    }

    private String getNotFoundResponse() {
        String body = "404 Not Found";

        return String.join("\r\n",
                "HTTP/1.1 404 Not Found",
                "Content-Type: text/plain; charset=utf-8",
                "Content-Length: " + body.getBytes(UTF_8).length,
                "",
                body
        );
    }

    private String getUnauthorizedRedirectHeaders(String location) {
        return "HTTP/1.1 302 Unauthorized\r\n"
                + "Location: " + location + "\r\n"
                + "Content-Length: 0\r\n"
                + "\r\n";
    }

    private String buildRedirectHeaders(String location) {
        return "HTTP/1.1 302 Found\r\n"
                + "Location: " + location + "\r\n"
                + "Content-Length: 0\r\n"
                + "\r\n";
    }

    private String buildRedirectHeaders(String location, String cookie) {
        return "HTTP/1.1 302 Found\r\n"
                + "Location: " + location + "\r\n"
                + "Content-Length: 0\r\n"
                + "Set-Cookie: " + cookie + "; Path=/; HttpOnly\r\n"
                + "\r\n";
    }


    private String getRequest(BufferedReader reader) throws IOException {
        final var request = reader.readLine();
        if (request == null || request.isBlank()) {
            throw new IllegalArgumentException("[ERROR] request is empty: " + request);
        }

        return request.trim();
    }

    private String[] getRequestLineParts(String request) {
        String[] requestLineParts = request.split(" ");
        if (requestLineParts.length < 3) {
            throw new IllegalArgumentException("[ERROR] invalid request: " + request);
        }

        return requestLineParts;
    }

    private void sendResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes(UTF_8));
        outputStream.flush();
    }

    private void authenticateUserFromRequestPath(String requestPath, OutputStream outputStream) throws IOException {
        int index = requestPath.indexOf(QUERY_PARAM);
        String queryString = requestPath.substring(index + 1);

        Map<LoginParam, String> accountAndPassword = queryParser(queryString);

        String account = accountAndPassword.get(LoginParam.ACCOUNT);
        String password = accountAndPassword.get(LoginParam.PASSWORD);

        if (InMemoryUserRepository.existsByAccount(account)) {
            User user = InMemoryUserRepository.findByAccount(account).get();
            log.info("User: account = {}, password = {}", account, password);

            if (user.isPasswordCorrect(password)) {
                String cookieSession = getSession(user);
                sendResponse(outputStream, buildRedirectHeaders("/index.html", cookieSession));
                return;
            }
        }

        sendResponse(outputStream, getUnauthorizedRedirectHeaders("/401.html"));
    }

    private void registerUser(String userInformation, OutputStream outputStream) throws IOException {
        Map<LoginParam, String> registerInformation = parseRequestBody(userInformation);
        if (InMemoryUserRepository.existsByAccount(registerInformation.get(LoginParam.ACCOUNT))) {
            sendResponse(outputStream, buildRedirectHeaders("/register.html"));
            return;
        }

        String account = registerInformation.get(LoginParam.ACCOUNT);
        String email = registerInformation.get(LoginParam.EMAIL);
        String password = registerInformation.get(LoginParam.PASSWORD);
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        sendResponse(outputStream, buildRedirectHeaders("/index.html"));
    }

    private Map<LoginParam, String> parseRequestBody(String requestBody) {
        Map<LoginParam, String> userInformation = new HashMap<>();
        String[] keyValuePairs = requestBody.split("&");

        for (String pair : keyValuePairs) {
            validateContainsEqual(pair);

            String[] keyValue = pair.split("=");
            String key = keyValue[0];
            String encodedValue = keyValue[1];

            LoginParam loginParam = LoginParam.getLoginParam(key);
            String decodedValue = URLDecoder.decode(encodedValue, UTF_8);

            userInformation.put(loginParam, decodedValue);
        }

        return userInformation;
    }

    private Map<LoginParam, String> queryParser(String queryString) {
        String[] accountAndPassword = queryString.split("&");
        validateContainsEqual(accountAndPassword[0]);
        validateContainsEqual(accountAndPassword[1]);

        String[] accountInfo = accountAndPassword[0].split("=");
        String[] passwordInfo = accountAndPassword[1].split("=");

        String account = accountInfo[1];
        String password = passwordInfo[1];

        return Map.of(
                LoginParam.ACCOUNT, account,
                LoginParam.PASSWORD, password
        );
    }

    private void validateUserCookie(String cookieRequest) throws IOException {
        if (sessionManager.isExistSessionId(cookieRequest)) {
            sessionManager.findSession(cookieRequest);
        }
    }

    private String getSession(User user) {
        String cookieSession = httpCookie.getCookieSession();
        Session session = new Session(cookieSession);

        session.setAttribute("user", user);
        sessionManager.add(session);

        return cookieSession;
    }

    private int toInt(String contentLength) {
        try {
            return Integer.parseInt(contentLength);
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("[ERROR] invalid content length");
        }
    }

    private void validateContainsEqual(String query) {
        if (!query.contains("=")) {
            throw new IllegalArgumentException("[ERROR] invalid query format");
        }
    }
}
