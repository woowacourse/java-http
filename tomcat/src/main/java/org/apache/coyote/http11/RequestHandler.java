package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    public static final String QUERYSTRING_SEPARATOR = "&";
    public static final String KEY_VALUE_SEPARATOR = "=";
    public static final int QUERYPARAMETER_KEY_INDEX = 0;
    public static final int QUERYPARAMETER_VALUE_INDEX = 1;

    private final RequestParser requestParser;
    private final String requestUri;
    private final SessionManager sessionManager;

    public RequestHandler(RequestParser requestParser) throws IOException {
        this.requestParser = requestParser;
        this.requestUri = requestParser.getRequestUri();
        this.sessionManager = SessionManager.getSessionManager();
    }

    public String getResponse() throws IOException {
        if (Objects.equals(requestUri, "/")) {
            return generate200Response("/", "Hello world!");
        }
        if (requestUri.startsWith("/register")) {
            return generateRegisterResponse();
        }
        if (requestUri.startsWith("/login")) {
            return generateLoginResponse();
        }
        String responseBody = generateResponseBody("static" + requestUri);
        return generate200Response(requestUri, responseBody);
    }

    private String generateRegisterResponse() throws IOException {
        if ("GET".equals(requestParser.getMethod())) {
            return getResponseBasedOnSession();
        }
        String body = requestParser.getBody();
        Optional<Map<String, String>> parsed = parseQueryString(body);
        Map<String, String> queryPairs = parsed.orElseThrow(() -> new NoSuchElementException("invalid query string"));
        register(queryPairs);
        return generate302Response("/index.html");
    }

    private void register(Map<String, String> parsed) {
        validateRegisterKeys(parsed);
        User newbie = new User(
                parsed.get("account"),
                parsed.get("password"),
                parsed.get("email")
        );
        InMemoryUserRepository.save(newbie);
    }

    private void validateRegisterKeys(Map<String, String> parsed) {
        Set<String> registerKeys = Set.of("account", "password", "email");
        boolean allKeysPresent = registerKeys.stream()
                .allMatch(parsed::containsKey);
        if (!allKeysPresent) {
            throw new NoSuchElementException("invalid query string");
        }
    }

    private String generateLoginResponse() throws IOException {
        if ("GET".equals(requestParser.getMethod())) {
            return getResponseBasedOnSession();
        }
        String body = requestParser.getBody();
        Optional<Map<String, String>> parsed = parseQueryString(body);
        Map<String, String> parsedQueryString = parsed.orElseThrow(
                () -> new NoSuchElementException("invalid query string")
        );
        return login(parsedQueryString);
    }

    private String getResponseBasedOnSession() throws IOException {
        String sessionId = requestParser.getCookie("JSESSIONID");
        if (sessionManager.hasSession(sessionId)) {
            return generate302Response("/index.html");
        }
        String responseBody = generateResponseBody("static" + requestUri);
        return generate200Response(requestUri, responseBody);
    }

    private String login(Map<String, String> parsedQueryString) {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(parsedQueryString.get("account"));
        if (optionalUser.isEmpty()) {
            return generate302Response("/401.html");
        }
        User user = optionalUser.get();
        if (user.checkPassword(parsedQueryString.get("password"))) {
            log.info("로그인 성공! 아이디 : {}", optionalUser.get().getAccount());
            final var session = getSession();
            session.setAttribute("user", user);
            return HttpCookie.appendSetCookieHeader(generate302Response("/index.html"), session.getId());
        }
        return generate302Response("/401.html");
    }

    private String generateResponseBody(String path) throws IOException {
        if (!path.contains(".")) {
            final URL resource = getClass().getClassLoader().getResource(path + ".html");
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        }
        final URL resource = getClass().getClassLoader().getResource(path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private Optional<Map<String, String>> parseQueryString(String queryString) {
        String[] queryParameters = queryString.split(QUERYSTRING_SEPARATOR);

        Map<String, String> keyValue = new HashMap<>();
        for (String queryParameter : queryParameters) {
            if (!queryParameter.contains(KEY_VALUE_SEPARATOR)) {
                return Optional.empty();
            }
            String[] pair = queryParameter.split(KEY_VALUE_SEPARATOR, -1);
            keyValue.put(pair[QUERYPARAMETER_KEY_INDEX], pair[QUERYPARAMETER_VALUE_INDEX]);
        }
        return Optional.of(keyValue);
    }

    private String generate200Response(String requestUri, String responseBody) {
        var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        if (requestUri.startsWith("/css")) {
            response = response.replace("text/html", "text/css");
        }
        return response;
    }

    private String generate302Response(String location) {
        var response = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: " + location,
                "Content-Type: text/html;charset=utf-8 "
        );
        return response;
    }

    public Session getSession() {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());
        sessionManager.add(session);
        return session;
    }
}