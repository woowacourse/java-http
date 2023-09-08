package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpResponseEntity;
import org.apache.coyote.http11.session.HttpSession;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = SessionManager.create();
    private static final String STATIC = "static";
    private static final String INDEX_HTML = "/index.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";
    private static final String LOGIN_HTML = "/login.html";
    private static final String BODY_DELIMITER = "&";
    private static final String PAIR_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

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
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final HttpCookie httpCookie = makeHttpCookie(httpRequest);

            final HttpResponseEntity httpResponseEntity = makeResponseEntity(httpRequest, httpCookie);
            final String response = httpResponseEntity.makeResponse();

            bufferedWriter.write(response);
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpCookie makeHttpCookie(final HttpRequest httpRequest) {
        if (httpRequest.hasCookie()) {
            return HttpCookie.from(httpRequest.getCookie());
        }
        return HttpCookie.empty();
    }

    private HttpResponseEntity makeResponseEntity(final HttpRequest httpRequest, final HttpCookie cookie) throws IOException {
        final HttpRequestStartLine startLine = httpRequest.getStartLine();
        final String path = startLine.getPath();

        if (startLine.getHttpMethod().equals(HttpMethod.POST)) {
            if (path.startsWith("/login")) {
                return login(httpRequest);
            }
            if (path.startsWith("/register")) {
                return register(httpRequest);
            }
        }
        if (path.startsWith("/login") && cookie.hasJSESSIONID()) {
            final String jsessionid = cookie.getJSESSIONID();
            HttpSession httpSession = sessionManager.findSession(jsessionid);
            if (Objects.isNull(httpSession)) {
                return HttpResponseEntity.ok(LOGIN_HTML, makeResponseBody(LOGIN_HTML));
            }
            return HttpResponseEntity.found(INDEX_HTML);
        }
        return HttpResponseEntity.ok(path, makeResponseBody(path));
    }

    private HttpResponseEntity login(final HttpRequest httpRequest) throws IOException {
        final Map<String, String> loginData = parseFormData(httpRequest.getBody());
        final User user = InMemoryUserRepository.findByAccount(loginData.get("account"))
                .orElseThrow();
        if (user.checkPassword(loginData.get("password"))) {
            final HttpCookie newCookie = HttpCookie.create();
            saveSession(newCookie, user);
            return HttpResponseEntity.found(INDEX_HTML)
                    .setCookie(newCookie.getJSESSIONID());
        }
        return HttpResponseEntity.ok(UNAUTHORIZED_HTML, makeResponseBody(UNAUTHORIZED_HTML));
    }

    private void saveSession(final HttpCookie newCookie, final User user) {
        final HttpSession httpSession = new HttpSession(newCookie.getJSESSIONID());
        httpSession.setAttribute("user", user);
        sessionManager.add(httpSession);
    }

    private HttpResponseEntity register(final HttpRequest httpRequest) throws IOException {
        final Map<String, String> registerData = parseFormData(httpRequest.getBody());
        InMemoryUserRepository.save(new User(registerData.get("account"), registerData.get("password"), registerData.get("email")));
        return HttpResponseEntity.ok(INDEX_HTML, makeResponseBody(INDEX_HTML));
    }

    private Map<String, String> parseFormData(final String body) {
        return Arrays.stream(body.split(BODY_DELIMITER))
                .map(data -> data.split(PAIR_DELIMITER))
                .collect(Collectors.toMap(
                        data -> data[KEY_INDEX],
                        data -> data[VALUE_INDEX])
                );
    }

    private String makeResponseBody(final String path) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }
        final ClassLoader classLoader = getClass().getClassLoader();
        final String filePath = classLoader.getResource(STATIC + path).getPath();
        final String fileContent = new String(Files.readAllBytes(Path.of(filePath)));
        return String.join("\r\n", fileContent);
    }
}
