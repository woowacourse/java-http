package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.HttpException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.requestline.HttpMethod;
import org.apache.coyote.http11.request.requestline.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_DIRECTORY = "static";
    private static final String USER = "user";

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_PATH = "; Path=/";

    private static final int END_OF_STREAM = -1;
    private static final int LINE_FEED = '\n';
    private static final String CARRIAGE_RETURN = "\r";

    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final String REGISTER_PATH = "/register";
    private static final String REGISTER_PAGE = "/register.html";

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

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
        try (final var inputStream = new BufferedInputStream(connection.getInputStream());
             final var outputStream = connection.getOutputStream()) {

            handle(inputStream, outputStream);
        } catch (IOException | UncheckedServletException |URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handle(final InputStream inputStream, final OutputStream outputStream)
            throws IOException, URISyntaxException {
        try {
            final String rawRequestLine = readLine(inputStream);
            final RequestLine requestLine = RequestLine.from(rawRequestLine);
            final RequestHeaders headers = RequestHeaders.from(readHeaders(inputStream));
            final RequestBody body = RequestBody.from(readBody(inputStream, headers.getContentLength()));
            final HttpRequest request = HttpRequest.of(requestLine, headers, body, sessionManager);

            log.info("request: {}", rawRequestLine);

            final HttpResponse response = route(request);
            addSessionCookie(request, response);
            response.writeTo(outputStream);
        } catch (HttpException e) {
            log.warn("invalid request [{}]: {}", e.getStatus().getCode(), e.getMessage());
//            HttpResponse.error().writeTo(outputStream);
        }
    }


    private void addSessionCookie(final HttpRequest request, final HttpResponse response) {
        request.getNewSession().ifPresent(session -> {
            log.debug("issue JSESSIONID: {}", session.getId());
            response.addCookie(JSESSIONID + "=" + session.getId() + COOKIE_PATH);
        });
    }

    private HttpResponse route(final HttpRequest request)
            throws IOException, URISyntaxException {
        final String path = request.getPath();

        if (ROOT_PATH.equals(path)) {
            return HttpResponse.ok(ContentType.HTML, "Hello world!".getBytes(UTF_8));
        }

        if (LOGIN_PATH.equals(path)) {
            if (request.isMethod(HttpMethod.POST)) {
                return HttpResponse.redirect(login(request));
            }
            if (isLoggedIn(request)) {
                return HttpResponse.redirect(INDEX_PAGE);
            }
            return staticFile(LOGIN_PAGE);
        }

        if (REGISTER_PATH.equals(path)) {
            if (request.isMethod(HttpMethod.POST)) {
                return HttpResponse.redirect(register(request));
            }
            return staticFile(REGISTER_PAGE);
        }

        return staticFile(path);
    }

    private boolean isLoggedIn(final HttpRequest request) {
        return request.findSession()
                .map(session -> session.getAttribute(USER))
                .isPresent();
    }

    private HttpResponse staticFile(final String filePath)
            throws IOException, URISyntaxException {
        final Optional<Path> found = findStaticFile(filePath);
        if (found.isEmpty()) {
            return HttpResponse.notFound(ContentType.HTML, readNotFoundBody());
        }
        return HttpResponse.ok(ContentType.from(filePath), Files.readAllBytes(found.get()));
    }

    private byte[] readNotFoundBody() throws IOException, URISyntaxException {
        final Optional<Path> notFoundPage = findStaticFile(NOT_FOUND_PAGE);
        if (notFoundPage.isPresent()) {
            return Files.readAllBytes(notFoundPage.get());
        }
        return "Not Found".getBytes(UTF_8);
    }

    private Optional<Path> findStaticFile(final String url) throws URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource(STATIC_DIRECTORY + url);
        if (resource == null) {
            return Optional.empty();
        }

        final Path path = Path.of(resource.toURI());
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        return Optional.of(path);
    }

    private String register(HttpRequest request) {
        final Optional<String> account = request.getParameter(ACCOUNT);
        final Optional<String> password = request.getParameter(PASSWORD);
        final Optional<String> email = request.getParameter(EMAIL);
        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            log.info("회원 가입을 하기위해서는 셋 다 입력이 되어야 합니다.");
            return REGISTER_PAGE;
        }
        InMemoryUserRepository.save(new User(account.get(), password.get(), email.get()));

        return INDEX_PAGE;
    }

    private List<String> readHeaders(final InputStream inputStream) throws IOException {
        final List<String> headers = new ArrayList<>();
        String line = readLine(inputStream);
        while (line != null && !line.isEmpty()) {
            headers.add(line);
            line = readLine(inputStream);
        }

        return headers;
    }

    private String readBody(
            final InputStream inputStream,
            final int contentLength
            ) throws IOException {
        if (contentLength == 0) {
            return "";
        }
        return new String(inputStream.readNBytes(contentLength), UTF_8);
    }

    private String readLine(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream line = new ByteArrayOutputStream();
        int read = inputStream.read();
        while (read != END_OF_STREAM && read != LINE_FEED) {
            line.write(read);
            read = inputStream.read();
        }
        if (read == END_OF_STREAM && line.size() == 0) {
            return null;
        }
        final String value = line.toString(UTF_8);
        if (value.endsWith(CARRIAGE_RETURN)) {
            return value.substring(0, value.length() - CARRIAGE_RETURN.length());
        }
        return value;
    }

    private String login(final HttpRequest request) {
        final Optional<String> account = request.getParameter(ACCOUNT);
        final Optional<String> password = request.getParameter(PASSWORD);
        if (account.isEmpty() || password.isEmpty()) {
            log.info("login parameters are missing");
            return UNAUTHORIZED_PAGE;
        }
        return InMemoryUserRepository.findByAccount(account.get())
                .filter(user -> user.checkPassword(password.get()))
                .map(user -> {
                    log.info("login success. account: {}", user.getAccount());
                    request.getSession().setAttribute(USER, user);
                    return INDEX_PAGE;
                })
                .orElseGet(() -> {
                    log.info("login failed. account: {}", account.get());
                    return UNAUTHORIZED_PAGE;
                });
    }
}
