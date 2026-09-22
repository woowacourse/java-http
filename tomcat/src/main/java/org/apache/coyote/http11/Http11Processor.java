package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.session.HttpCookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String ROOT_RESPONSE_BODY = "Hello world!";
    private static final String LOGIN_USER = "user";

    private final Socket connection;
    private final RequestParser requestParser;
    private final ResponseBuilder responseBuilder;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this(connection, new RequestParser());
    }

    public Http11Processor(final Socket connection, RequestParser requestParser) {
        this.connection = connection;
        this.requestParser = requestParser;
        this.responseBuilder = new ResponseBuilder();
        this.sessionManager = new SessionManager();
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Map<String, String> requestInformations = requestParser.parse(bufferedReader);
            HttpCookie cookie = new HttpCookie(requestParser.getCookie(requestInformations));

            String path = requestParser.getRequestPath(requestInformations);

            String response;
            if (path.equals(ROOT_PATH)) {
                response = responseBuilder.build(
                        HttpStatus.OK,
                        requestParser.getAccept(requestInformations),
                        processRootRequest()
                );
            } else if (path.equals(LOGIN_PATH)) {
                response = processLoginRequest(requestInformations, cookie);
            } else if (path.equals(REGISTER_PATH)) {
                response = processRegisterRequest(requestInformations);
            } else {
                response = responseBuilder.build(
                        HttpStatus.OK,
                        requestParser.getAccept(requestInformations),
                        processOtherRequest(path)
                );
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String processRootRequest() {
        return ROOT_RESPONSE_BODY;
    }

    private String processLoginRequest(Map<String, String> requestHeaderInfos, HttpCookie cookie)
            throws URISyntaxException, IOException {
        Map<String, String> queryParams = requestParser.getQueryParams(requestHeaderInfos);

        if (requestParser.getRequestMethod(requestHeaderInfos).equals(GET) && queryParams.isEmpty()) {
            if (isLoggedIn(cookie)) {
                return responseBuilder.buildRedirect(HttpStatus.FOUND, "/index.html");
            }
            return responseBuilder.build(
                    HttpStatus.OK,
                    requestParser.getAccept(requestHeaderInfos),
                    readStaticResource(LOGIN_PATH + ".html")
            );
        }

        User user = findLoginUser(queryParams);
        if (user != null) {
            Session session = getOrCreateSession(cookie);
            session.setAttribute(LOGIN_USER, user);
            return responseBuilder.buildWithCookie(HttpStatus.FOUND, "/index.html", cookie, session.getId());
        }

        return responseBuilder.buildRedirect(HttpStatus.FOUND, "/401.html");
    }

    private boolean isLoggedIn(HttpCookie cookie) {
        Session session = sessionManager.findSession(cookie.getJSessionId());
        return session != null && session.getAttribute(LOGIN_USER) != null;
    }

    private String processRegisterRequest(Map<String, String> requestHeaderInfos)
            throws URISyntaxException, IOException {
        String requestMethod = requestParser.getRequestMethod(requestHeaderInfos);

        if (requestMethod.equals(GET)) {
            return responseBuilder.build(
                    HttpStatus.OK,
                    requestParser.getAccept(requestHeaderInfos),
                    readStaticResource(REGISTER_PATH + ".html")
            );
        }

        if (requestMethod.equals(POST)) {
            registerUser(requestParser.getQueryParams(requestHeaderInfos));
            return responseBuilder.buildRedirect(HttpStatus.FOUND, "/index.html");
        }

        return responseBuilder.build(
                HttpStatus.OK,
                requestParser.getAccept(requestHeaderInfos),
                ""
        );
    }

    private String processOtherRequest(String path) throws URISyntaxException, IOException {
        return readStaticResource(path);
    }

    private User findLoginUser(Map<String, String> queryParams) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");

        if (account == null || password == null) {
            return null;
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password))
                .orElse(null);
    }

    private Session getOrCreateSession(HttpCookie cookie) {
        Session session = sessionManager.findSession(cookie.getJSessionId());
        if (session != null) {
            return session;
        }

        Session newSession = new Session(UUID.randomUUID().toString());
        sessionManager.add(newSession);
        return newSession;
    }

    private void registerUser(Map<String, String> requestParams) {
        String account = requestParams.get("account");
        String password = requestParams.get("password");
        String email = requestParams.get("email");

        if (account == null || password == null || email == null) {
            return;
        }

        InMemoryUserRepository.save(new User(account, password, email));
    }

    private String readStaticResource(String path) throws URISyntaxException, IOException {
        String fileName = path.replaceFirst(ROOT_PATH, "");

        URL resource = findStaticResource(fileName);
        if (resource == null) {
            log.info("해당 리소스를 찾을 수 없습니다. 사유 : null | URL = {}", resource);
            return "";
        }
        return Files.readString(Path.of(resource.toURI()));
    }

    private URL findStaticResource(String fileName) {
        URL resource = getClass().getClassLoader().getResource("static/" + fileName);

        if (resource != null || fileName.endsWith(".html")) {
            return resource;
        }

        return getClass().getClassLoader().getResource("static/" + fileName + ".html");
    }
}
