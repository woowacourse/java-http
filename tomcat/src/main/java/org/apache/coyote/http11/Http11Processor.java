package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String GET = "GET";
    private static final String POST = "POST";

    private static final String STATIC_ROOT = "static";
    private static final String ROOT_PATH = "/";
    private static final String MIME_TYPE_DEFAULT = "text/html";
    private static final String MIME_TYPES_WILDCARD = "*/*";
    private static final Map<String, String> MIME_TYPE = Map.ofEntries(
            Map.entry("text/html", ".html"),
            Map.entry("text/css", ".css"),
            Map.entry("text/javascript", ".js")
    );

    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    private static final String JSESSIONID = "JSESSIONID";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionIdGenerator sessionIdGenerator;
    private final SessionManager sessionManager = SessionManager.getInstance();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.sessionIdGenerator = new SessionIdGenerator();
    }

    public Http11Processor(final Socket connection, SessionIdGenerator sessionIdGenerator) {
        this.connection = connection;
        this.sessionIdGenerator = sessionIdGenerator;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var br = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()
        ) {
            HttpRequest request = HttpRequest.from(br);
            final HttpResponse response = handle(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handle(HttpRequest request) throws IOException {
        HttpRequestStartLine startLine = request.startLine();
        HttpRequestHeader header = request.requestHeader();
        HttpCookie cookie = header.cookie();
        HttpRequestBody body = request.requestBody();

        String responseBody;

        String contentType = resolveContentType(header);
        URL url = findStaticResource(startLine.path(), contentType);

        if (url == null || url.getPath().endsWith(ROOT_PATH)) {
            responseBody = "Hello world!";

            return HttpResponse.ok().contentType(contentType).body(responseBody);
        }

        if (startLine.method().equals(GET) && startLine.path().endsWith("login")) {
            if (hasValidSession(cookie)) {
                return HttpResponse.found().location(INDEX_PAGE);
            }

            Path path = new File(url.getFile()).toPath();
            responseBody = Files.readString(path);
            return HttpResponse.ok().contentType(contentType).body(responseBody);
        }

        if (startLine.method().equals(POST) && startLine.path().endsWith("login")) {
            return handleLogin(header, body);
        }

        if (startLine.path().endsWith("register") && header.hasContain("Content-Length")) {
            String location = registerUser(body);
            return HttpResponse.found().location(location);
        }

        Path path = new File(url.getFile()).toPath();
        responseBody = Files.readString(path);
        return HttpResponse.ok().contentType(contentType).body(responseBody);
    }

    private String resolveContentType(HttpRequestHeader header) {
        String accept = header.header().get("Accept");

        if (accept == null || accept.isEmpty()) {
            return MIME_TYPE_DEFAULT;
        }

        String preferred = accept.split(",")[0].split(";")[0].trim();

        if (MIME_TYPES_WILDCARD.equals(preferred)) {
            return MIME_TYPE_DEFAULT;
        }

        return preferred;
    }

    private URL findStaticResource(String path, String contentType) {
        if (path.contains(".")) {
            return getClass().getClassLoader().getResource(STATIC_ROOT + path);
        }

        return getClass().getClassLoader().getResource(STATIC_ROOT + path + MIME_TYPE.get(contentType));
    }

    private User loginUser(HttpRequestBody body) {
        String[] formData = body.requestBody().split("&");

        List<String> data = Arrays.asList(formData);

        String account = data.get(0).split("=")[1];
        String password = data.get(1).split("=")[1];

        User user = InMemoryUserRepository.findByAccount(account)
                .orElse(null);

        if (user == null || !user.checkPassword(password)) {
            return null;
        }

        log.info("user : {}", user);

        return user;
    }

    private String registerUser(HttpRequestBody body) {
        String[] formData = body.requestBody().split("&");

        List<String> data = Arrays.asList(formData);

        String account = data.get(0).split("=")[1];
        String email = data.get(1).split("=")[1];
        String password = data.get(2).split("=")[1];

        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);

        log.info("new user : {}", newUser);

        return INDEX_PAGE;
    }

    private HttpResponse handleLogin(HttpRequestHeader header, HttpRequestBody body) {
        if (!header.hasContain("Content-Length")) {
            return HttpResponse.status(HttpStatus.LENGTH_REQUIRED);
        }

        User user = loginUser(body);
        if (user == null) {
            return HttpResponse.found().location(UNAUTHORIZED_PAGE);
        }

        String sessionId = sessionIdGenerator.generate();
        sessionManager.add(new Session(sessionId, "user", user));
        return HttpResponse.found().location(INDEX_PAGE).setCookie(JSESSIONID, sessionId);
    }

    private boolean hasValidSession(HttpCookie cookie) {
        if (!cookie.hasJSessionId()) {
            return false;
        }

        return sessionManager.hasUser(cookie.getJSessionId());
    }
}
