package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.NotCorrectPasswordException;
import org.apache.coyote.http11.exception.NotFoundAccountException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String ROOT_RESOURCE = "Hello world!";
    private static final String STATIC_PATH = "static";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String LOGIN_PATH = "/login.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String INTERNAL_SERVER_PATH = "/500.html";
    private static final String NOT_FOUND_PATH = "/404.html";
    private static final String INDEX = "/index.html";
    private static final String REGISTER_PATH = "/register";
    private static final String EMAIL = "email";

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest httpRequest = HttpRequest.from(br);
            final HttpResponse response = getRespond(httpRequest);

            outputStream.write(response.toResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getRespond(final HttpRequest httpRequest) {
        try {
            return getRespondByMethod(httpRequest);
        } catch (NotFoundAccountException | NotCorrectPasswordException e) {
            return foundResponse(httpRequest, UNAUTHORIZED_PATH);
        } catch (Exception e) {
            return foundResponse(httpRequest, INTERNAL_SERVER_PATH);
        }
    }

    private HttpResponse getRespondByMethod(final HttpRequest httpRequest) throws IOException {
        final HttpMethod method = httpRequest.getRequestURL().getMethod();
        if (method == HttpMethod.GET) {
            return getResponse(httpRequest);
        }
        if (method == HttpMethod.POST) {
            return postResponse(httpRequest);
        }
        return foundResponse(httpRequest, NOT_FOUND_PATH);
    }

    public HttpResponse getResponse(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.getRequestURL().getUrl().equals(ROOT_PATH)) {
            return okResponse(httpRequest, ROOT_RESOURCE);
        }
        final String requestUrl = httpRequest.getRequestURL().getAbsolutePath();
        final String responseBody = ResourceReader.readResource(STATIC_PATH + requestUrl);
        if (requestUrl.equals(LOGIN_PATH) && isLoggedIn(httpRequest)) {
            return foundResponse(httpRequest, INDEX);
        }
        return okResponse(httpRequest, responseBody);
    }

    private HttpResponse postResponse(final HttpRequest httpRequest) throws IOException{
        final String url = httpRequest.getRequestURL().getUrl();
        if (url.equals(REGISTER_PATH)) {
            final Map<String, String> requestParam = Arrays.stream(httpRequest.getRequestBody().split("&")).takeWhile(it -> !it.isEmpty())
                    .map(it -> it.split("=")).collect(Collectors.toMap(it -> it[0], it -> it[1]));
            final User registeredUser = new User(requestParam.get(ACCOUNT), requestParam.get(PASSWORD), requestParam.get(EMAIL));

            InMemoryUserRepository.save(registeredUser);
            return foundResponse(httpRequest, INDEX);
        }
        final String requestUrl = httpRequest.getRequestURL().getAbsolutePath();
        final String responseBody = ResourceReader.readResource(STATIC_PATH + requestUrl);
        if (requestUrl.equals(LOGIN_PATH)) {
            if(isLoggedIn(httpRequest)){
                return foundResponse(httpRequest, INDEX);
            }
            return renderLogin(httpRequest);
        }
        return okResponse(httpRequest, responseBody);
    }

    private boolean isLoggedIn(final HttpRequest httpRequest) {
        Session session = findSession(httpRequest);
        return session != null;
    }

    private Session findSession(final HttpRequest httpRequest) {
        SessionManager sessionManager = new SessionManager();
        String jSessionId = HttpCookie.parseCookie(httpRequest.getRequestHeaders().getValue("Cookie")).getValue("JSESSIONID");

        if (jSessionId == null) {
            return null;
        }
        return sessionManager.findSession(jSessionId);
    }

    private HttpResponse renderLogin(final HttpRequest httpRequest) throws IOException {
        final String requestBody = httpRequest.getRequestBody();

        final Map<String, String> collect = Arrays.stream(requestBody.split("&")).takeWhile(it -> !it.isEmpty())
                .map(it -> it.split("=")).collect(Collectors.toMap(it -> it[0], it -> it[1]));
        //로그인 유저 검증
        final String url = httpRequest.getRequestURL().getAbsolutePath();

        if (!collect.isEmpty()) {
            return login(httpRequest, collect);
        }

        return okResponse(httpRequest, ResourceReader.readResource(STATIC_PATH + url));
    }

    private HttpResponse login(final HttpRequest httpRequest, final Map<String, String> requestParam) {
        final User user = InMemoryUserRepository.findByAccount(requestParam.get(ACCOUNT))
                .orElseThrow(NotFoundAccountException::new);

        if (!user.checkPassword(requestParam.get(PASSWORD))) {
            throw new NotCorrectPasswordException();
        }

        log.info(user.toString());

        return foundResponse(httpRequest, INDEX);
    }

    private HttpResponse okResponse(final HttpRequest httpRequest, final String responseBody) {
        final Map<String, String> responseHeaders = new LinkedHashMap<>();

        responseHeaders.put("Content-Type", HttpContentType.valueOfCotentType(httpRequest.getRequestURL().getExtension()).getContentType());
        if (!responseBody.isBlank()) {
            responseHeaders.put("Content-Length", responseBody.getBytes().length + " ");
        }

        return new HttpResponse(HttpStatus.OK, responseHeaders, responseBody);
    }

    private HttpResponse foundResponse(final HttpRequest httpRequest, final String location) {
        final Map<String, String> responseHeaders = new HashMap<>();
        setCookie(httpRequest,responseHeaders);
        responseHeaders.put("Location", location);
        return new HttpResponse(HttpStatus.FOUND, responseHeaders, "");
    }

    private void setCookie(final HttpRequest httpRequest, final Map<String, String> responseHeaders) {
        final HttpCookie cookie = HttpCookie.parseCookie(httpRequest.getRequestHeaders().getValue("Cookie"));
        if (!cookie.checkIdInCookie()) {
            final UUID sessionId = UUID.randomUUID();
            createSession(sessionId.toString());
            responseHeaders.put("Set-Cookie", cookie.makeCookieValue(sessionId));
        }
    }

    private void createSession(final String sessionId) {
        Session session = new Session(sessionId);
        SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);
    }
}
