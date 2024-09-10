package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    public static final String HTML_SUFFIX = ".html";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String JSESSIONID = "JSESSIONID";
    public static final String STATIC_PATH = "static";
    public static final String INDEX_PAGE = "/index.html";
    public static final String ACCESS_DENIED_PAGE = "/401.html";
    public static final String CSS_PATH = "/css/styles.css";
    public static final String JS_PATH = "/js/scripts.js";
    public static final String LOGIN_PATH = "/login";
    public static final String REGISTER_PATH = "/register";
    public static final String ASSETS_PATH = "/assets/.*\\.js";
    public static final String LINE_BREAK = "\n";
    public static final String USER_SESSION_NAME = "user";
    public static final String ACCOUNT_PARAM_NAME = "account";
    public static final String PASSWORD_PARAM_NAME = "password";
    public static final String EMAIL_PARAM_NAME = "email";

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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
            final var request = HttpRequest.from(reader);
            if (request.getRequestLine() == null) {
                return;
            }

            final var method = request.getRequestMethod();
            final var url = request.getRequestPath();

            log.info("request method: {}, request url: {}", method, url);

            if ("/".equals(url)) {
                outputStream.write(HttpResponse.ok(FileType.HTML, "Hello world!"));
            }
            if (INDEX_PAGE.equals(url)) {
                buildHtmlResponse(outputStream, url);
            }
            if (CSS_PATH.equals(url)) {
                buildStyleSheetResponse(outputStream, url);
            }
            if (JS_PATH.equals(url)) {
                buildScriptResponse(outputStream, url);
            }
            if (url.matches(ASSETS_PATH)) {
                buildScriptResponse(outputStream, url);
            }
            if (LOGIN_PATH.equals(url) && GET.name().equals(method) && !isLogin(request, outputStream)) {
                buildHtmlResponse(outputStream, url);
            }
            if (LOGIN_PATH.equals(url) && POST.name().equals(method)) {
                login(outputStream, request);
            }
            if (REGISTER_PATH.equals(url) && GET.name().equals(method)) {
                buildHtmlResponse(outputStream, url);
            }
            if (REGISTER_PATH.equals(url) && POST.name().equals(method)) {
                register(outputStream, request);
            }

            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean isLogin(final HttpRequest request, final OutputStream outputStream) throws IOException {
        final var cookies = request.getCookies();
        return cookies.contains(JSESSIONID) && hasSession(outputStream, cookies);
    }

    private boolean hasSession(final OutputStream outputStream, final HttpCookie cookies) throws IOException {
        final var sessionId = cookies.getCookieValue(JSESSIONID);
        if (SessionManager.findSession(sessionId) != null) {
            outputStream.write(HttpResponse.found(INDEX_PAGE));
            outputStream.flush();
            return true;
        }
        return false;
    }

    private void login(final OutputStream outputStream, final HttpRequest request) {
        Map<String, String> params = request.parseRequestQuery();

        InMemoryUserRepository.findByAccount(params.get(ACCOUNT_PARAM_NAME)).ifPresentOrElse(user -> {
            if (user.checkPassword(params.get(PASSWORD_PARAM_NAME))) {
                Session session = createSession(user);
                addCookieOnResponseHeader(outputStream, session);
                log.info("user: {}", user);
                return;
            }
            buildHtmlResponse(outputStream, ACCESS_DENIED_PAGE);
        }, () -> buildHtmlResponse(outputStream, ACCESS_DENIED_PAGE));
    }

    private Session createSession(final User user) {
        final UUID id = UUID.randomUUID();
        final Session session = new Session(id.toString());
        session.setAttribute(USER_SESSION_NAME, user);
        SessionManager.add(session);

        return session;
    }

    private void addCookieOnResponseHeader(final OutputStream outputStream, final Session session) {
        try {
            HttpCookie cookie = HttpCookie.from(session);
            log.info("cookie: {}", session.getId());
            outputStream.write(HttpResponse.found(INDEX_PAGE, cookie));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void register(final OutputStream outputStream, final HttpRequest request) throws IOException {
        final Map<String, String> userInfos = request.parseRequestQuery();

        final User user = new User(userInfos.get(ACCOUNT_PARAM_NAME),
                userInfos.get(PASSWORD_PARAM_NAME),
                userInfos.get(EMAIL_PARAM_NAME)
        );
        InMemoryUserRepository.save(user);

        outputStream.write(HttpResponse.found(INDEX_PAGE));
    }

    private String buildResponseBodyFromStaticFile(final String filePath) throws IOException {
        final var resourceName = STATIC_PATH + filePath;
        final var path = Path.of(this.getClass().getClassLoader().getResource(resourceName).getPath());

        return String.join(LINE_BREAK, Files.readAllLines(path)) + LINE_BREAK;
    }

    private void buildHtmlResponse(final OutputStream outputStream, final String filePath) {
        try {
            if (filePath.endsWith(HTML_SUFFIX)) {
                final var responseBody = buildResponseBodyFromStaticFile(filePath);
                outputStream.write(HttpResponse.ok(FileType.HTML, responseBody));
                return;
            }
            final var responseBody = buildResponseBodyFromStaticFile(filePath + HTML_SUFFIX);
            outputStream.write(HttpResponse.ok(FileType.HTML, responseBody));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void buildStyleSheetResponse(final OutputStream outputStream, final String fileName) throws IOException {
        final var responseBody = buildResponseBodyFromStaticFile(fileName);

        outputStream.write(HttpResponse.ok(FileType.CSS, responseBody));
    }

    private void buildScriptResponse(final OutputStream outputStream, final String fileName) throws IOException {
        final var responseBody = buildResponseBodyFromStaticFile(fileName);

        outputStream.write(HttpResponse.ok(FileType.JAVASCRIPT, responseBody));
    }
}
