package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.HttpCookie;
import nextstep.jwp.model.HttpRequest;
import nextstep.jwp.model.HttpResponse;
import nextstep.jwp.model.Session;
import nextstep.jwp.model.SessionManager;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_PAGE_URL = "/login.html";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PAGE_URL = "/register.html";
    private static final String REGISTER_PATH = "/register";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final User EMPTY_USER = new User(null, null, null);
    private static final String POST = "POST";
    private static final String GET = "GET";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final HttpResponse response = handle(httpRequest);

            outputStream.write(response.toResponse().getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final String method = httpRequest.getRequestLine().getMethod();
        if (method.equals(POST)) {
            return post(httpRequest);
        }
        return get(httpRequest);
    }

    private HttpResponse post(final HttpRequest httpRequest) throws IOException {
        final String httpVersion = httpRequest.getRequestLine().getVersion();
        final String path = httpRequest.getRequestLine().getPath();

        final HttpResponse response = new HttpResponse();
        response.setResponseBody(getResponseBody(path));
        response.addContentType(getContentType(path));
        response.addContentType("charset=utf-8");

        if (path.equals(LOGIN_PAGE_URL)) {
            final Parameters loginParameters = Parameters.parseParameters(httpRequest.getRequestBody(), "&");
            final String account = loginParameters.get(ACCOUNT);
            final String password = loginParameters.get(PASSWORD);

            final User user = getUser(account);
            if (user.checkPassword(password)) {
                log.info(user.toString());
                final Session session = createSession(user);
                response.setCookie("JSESSIONID=" + session.getId());
                response.sendRedirect(httpVersion, "/index.html");
                return response;
            }
            response.sendRedirect(httpVersion, "/401.html");
            return response;
        }
        if (path.equals(REGISTER_PAGE_URL)) {
            final Parameters loginParameters = Parameters.parseParameters(httpRequest.getRequestBody(), "&");
            final String account = loginParameters.get(ACCOUNT);
            final String password = loginParameters.get(PASSWORD);
            final String email = loginParameters.get(EMAIL);
            final User user = new User(account, password, email);

            InMemoryUserRepository.save(user);
            response.sendRedirect(httpVersion, "/index.html");
            return response;
        }

        response.sendOk(httpVersion);
        return response;
    }

    private Session createSession(final User user) {
        final UUID uuid = UUID.randomUUID();
        final Session session = new Session(uuid.toString());
        SESSION_MANAGER.add(session);
        session.setAttribute("user", user);

        return session;
    }

    private HttpResponse get(final HttpRequest httpRequest) throws IOException {
        final String httpVersion = httpRequest.getRequestLine().getVersion();
        final String path = httpRequest.getRequestLine().getPath();

        final HttpResponse response = new HttpResponse();
        response.setResponseBody(getResponseBody(path));
        response.addContentType(getContentType(path));
        response.addContentType("charset=utf-8");

        if (path.equals(LOGIN_PAGE_URL) && alreadyLogin(httpRequest)) {
            response.sendRedirect(httpVersion, "/index.html");
            return response;
        }
        response.sendOk(httpVersion);
        return response;
    }

    private boolean alreadyLogin(final HttpRequest httpRequest) {
        final String jSessionId = getJSessionId(httpRequest);
        if (jSessionId == null) {
            return false;
        }
        final Session session = SESSION_MANAGER.findSession(jSessionId);
        if (session == null) {
            return false;
        }
        final User user = getUser(session);
        return !user.equals(EMPTY_USER);
    }

    private String getJSessionId(final HttpRequest httpRequest) {
        final String cookieHeader = httpRequest.getRequestHeader().get("Cookie");
        if (cookieHeader == null) {
            return null;
        }
        final Parameters cookies = Parameters.parseParameters(cookieHeader, ";");
        final HttpCookie httpCookie = HttpCookie.from(cookies);
        return httpCookie.getJSessionId();
    }

    private String getContentType(final String path) throws IOException {
        final String contentType = Files.probeContentType(Path.of(path));
        if (contentType == null) {
            return DEFAULT_CONTENT_TYPE;
        }
        return contentType;
    }

    private User getUser(final String account) {
        if (account == null || account.isBlank()) {
            return EMPTY_USER;
        }
        try {
            return InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException(account + "가 없습니다."));
        } catch (final IllegalArgumentException e) {
            log.info(e.getMessage());
            return EMPTY_USER;
        }
    }

    private User getUser(final Session session) {
        if (session.containsAttribute("user")) {
            return (User) session.getAttribute("user");
        }
        return EMPTY_USER;
    }

    private String getResponseBody(final String path) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }

        return readFile(path);
    }

    private String readFile(final String url) throws IOException {
        final URL fileUrl = getClass().getClassLoader().getResource("static" + url);
        if (fileUrl == null) {
            throw new NoSuchFileException(url + " 파일이 없습니다.");
        }

        try (final FileInputStream fileInputStream = new FileInputStream(fileUrl.getPath())) {
            return new String(fileInputStream.readAllBytes());
        }
    }
}
