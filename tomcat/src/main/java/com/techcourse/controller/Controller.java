package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.session.SessionManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.startline.HttpMethod;
import org.apache.coyote.http11.startline.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private static final String DELIMITER = "\r\n";
    private static final String BASIC_RESPONSE_BODY = "Hello world!";

    public boolean service(HttpRequest request, HttpResponse response) {
        if (request.isTargetBlank()) {
            response.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
            response.setBody(BASIC_RESPONSE_BODY);
            return true;
        }
        if (isLoginPageRequest(request)) {
            return showLoginPage(request, response);
        }
        if (request.isTargetStatic()) {
            return readStaticFile(response, request.getTargetPath());
        }
        return createDynamicResponse(request, response);
    }

    private boolean isLoginPageRequest(HttpRequest request) {
        return request.targetEqualTo("/login") && request.isTargetStatic();
    }

    private boolean showLoginPage(HttpRequest request, HttpResponse response) {
        Optional<String> nullableSession = request.getSessionFromCookie();
        return nullableSession.map(SessionManager::findSession)
                .map(session -> redirectToIndex(response))
                .orElseGet(() -> readStaticFile(response, request.getTargetPath()));
    }

    private boolean createDynamicResponse(HttpRequest request, HttpResponse response) {
        if (isLoginRequest(request)) {
            return login(request, response);
        }
        if (isRegisterRequest(request)) {
            return register(request, response);
        }
        return false;
    }

    private boolean isLoginRequest(HttpRequest request) {
        return request.getHttpMethod() == HttpMethod.POST
                && request.containsBody()
                && request.targetStartsWith("/login");
    }

    public boolean login(HttpRequest request, HttpResponse response) {
        Optional<User> nullableUser = InMemoryUserRepository.findByAccount(request.getFromBody("account"));
        return nullableUser
                .filter(user -> user.checkPassword(request.getFromBody("password")))
                .map(user -> loginAndRedirectToIndex(response, user))
                .orElseGet(() -> redirectTo401(response));
    }

    private boolean isRegisterRequest(HttpRequest request) {
        return request.getHttpMethod() == HttpMethod.POST
                && request.containsBody()
                && request.targetStartsWith("/register");
    }

    private boolean register(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getFromBody("account"),
                request.getFromBody("password"),
                request.getFromBody("email")
        );
        InMemoryUserRepository.save(user);
        return loginAndRedirectToIndex(response, user);
    }

    private boolean loginAndRedirectToIndex(HttpResponse response, User user) {
        String jSessionId = SessionManager.addUser(user);
        response.addSessionToCookies(jSessionId);
        return redirectToIndex(response);
    }

    private boolean redirectToIndex(HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.addHeader(HttpHeader.LOCATION, "/index");
        return true;
    }

    private boolean redirectTo401(HttpResponse response) {
        URL resource = getClass().getClassLoader().getResource("static/401.html");
        try {
            Path path = Path.of(resource.toURI());
            response.setStatus(HttpStatus.UNAUTHORIZED);
            return readStaticFile(response, path);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("cannot convert to URI: " + resource);
        }
    }

    public boolean readStaticFile(HttpResponse response, Path path) {
        try {
            if (Files.exists(path)) {
                String responseBody = readFile(path);
                String contentType = Files.probeContentType(path);
                response.addHeader(HttpHeader.CONTENT_TYPE, contentType + ";charset=utf-8");
                response.setBody(responseBody);
                return response.isValid();
            }
            throw new FileNotFoundException(path.toString());
        } catch (NullPointerException | IOException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("invalid path: " + path.toString());
        }
    }

    public String readFile(Path path) throws IOException {
        List<String> fileLines = Files.readAllLines(path);

        StringJoiner joiner = new StringJoiner(DELIMITER);
        for (String fileLine : fileLines) {
            joiner.add(fileLine);
        }
        joiner.add("");
        return joiner.toString();
    }
}
