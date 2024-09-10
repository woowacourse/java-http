package org.apache.catalina.core;

import static org.apache.tomcat.util.http.RequestLine.HTTP_METHOD;
import static org.apache.tomcat.util.http.RequestLine.REQUEST_URI;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.HttpCookie;
import org.apache.tomcat.util.http.HttpStatus;
import org.apache.tomcat.util.http.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class StandardContext {

    private static final Logger log = LoggerFactory.getLogger(StandardContext.class);

    public static void processRequest(Map<RequestLine, String> requestLine, Map<String, String> headers, String body, HttpResponse response) {
        if (requestLine.get(REQUEST_URI).equals("/")) {
            responseIndexPage(HttpStatus.OK, response);
            return;
        }
        if (requestLine.get(REQUEST_URI).equals("/login") && requestLine.get(HTTP_METHOD).equals("GET")) {
            HttpCookie httpCookie = new HttpCookie(headers.get("Cookie"));
            SessionManager sessionManager = new SessionManager();
            String sessionId = httpCookie.get("JSESSIONID");
            Session session = sessionManager.findSession(sessionId);
            if (Objects.nonNull(session)) {
                responseIndexPage(HttpStatus.FOUND, response);
                return;
            }
            responseLoginPage(response);
            return;
        }
        if (requestLine.get(REQUEST_URI).equals("/login") && requestLine.get(HTTP_METHOD).equals("POST")) {
            login(headers, body, response);
            return;
        }
        if (requestLine.get(REQUEST_URI).equals("/register") && requestLine.get(HTTP_METHOD).equals("GET")) {
            responseRegisterPage(response);
            return;
        }
        if (requestLine.get(REQUEST_URI).equals("/register") && requestLine.get(HTTP_METHOD).equals("POST")) {
            register(body, response);
            return;
        }
        String content = findStaticFile(requestLine.get(REQUEST_URI));
        if (!StringUtils.isEmpty(content)) {
            responseStaticPage(requestLine, response, content);
        }
    }

    private static void responseIndexPage(HttpStatus httpStatus, HttpResponse response) {
        response.addHttpStatus(httpStatus);
        response.addHeader("Location", "/index.html");
        response.addHeader("Content-Type", probeContentType("/index.html"));
        response.setBody(findStaticFile("/index.html"));
    }

    private static void responseLoginPage(HttpResponse response) {
        String contentType = probeContentType("/login.html");
        String content = findStaticFile("/login.html");
        response.addHttpStatus(HttpStatus.OK);
        response.addHeader("Content-Type", contentType);
        response.setBody(content);
    }

    private static void responseRegisterPage(HttpResponse response) {
        String contentType = probeContentType("/register.html");
        String content = findStaticFile("/register.html");
        response.addHttpStatus(HttpStatus.OK);
        response.addHeader("Content-Type", contentType);
        response.setBody(content);
    }

    private static void responseStaticPage(Map<RequestLine, String> requestLine, HttpResponse response, String content) {
        String contentType = probeContentType(requestLine.get(REQUEST_URI));
        response.addHttpStatus(HttpStatus.OK);
        response.addHeader("Content-Type", contentType);
        response.setBody(content);
    }

    private static String probeContentType(String url) {
        try {
            return Files.probeContentType(Paths.get(Objects.requireNonNull(StandardContext.class.getClassLoader()
                    .getResource("static" + url)).toURI()));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String findStaticFile(String url) {
        try {
            URL resource = StandardContext.class.getClassLoader().getResource("static" + url);
            if (Objects.isNull(resource)) {
                return StringUtils.EMPTY;
            }
            return new String(Files.readAllBytes(new File((resource).getFile()).toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void login(Map<String, String> headers, String body, HttpResponse response) {
        Map<String, String> queryString = parseQueryStringType(body);
        HttpCookie httpCookie = new HttpCookie(headers.get("Cookie"));
        String account = queryString.get("account");
        String password = queryString.get("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty()) {
            loginWithInvalidAccount(response, account);
            return;
        }
        User user = optionalUser.get();
        if (user.checkPassword(password)) {
            normalLogin(httpCookie, response, user);
            return;
        }
        loginWithInvalidPassword(response, user, password);
    }

    private static void loginWithInvalidAccount(HttpResponse response, String account) {
        log.error("inputAccount={}, 해당하는 사용자를 찾을 수 없습니다.", account);
        response.addHttpStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/404.html");
        response.addHeader("Content-Type", probeContentType("/404.html"));
        response.setBody(findStaticFile("/404.html"));
    }

    private static void loginWithInvalidPassword(HttpResponse response, User user, String password) {
        log.error("user: {}, inputPassword={}, 비밀번호가 올바르지 않습니다.", user, password);
        response.addHttpStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/401.html");
        response.addHeader("Content-Type", probeContentType("/401.html"));
        response.setBody(findStaticFile("/401.html"));
    }

    private static void normalLogin(HttpCookie httpCookie, HttpResponse response, User user) {
        log.info("user: {}", user);
        SessionManager sessionManager = new SessionManager();
        String sessionId = httpCookie.get("JSESSIONID");
        if (Objects.isNull(sessionId) || Objects.isNull(sessionManager.findSession(sessionId))) {
            Session session = new Session(user);
            sessionManager.add(session);
            response.addHeader("Set-Cookie", "JSESSIONID=" + session.getSessionId());
        }
        responseIndexPage(HttpStatus.FOUND, response);
    }

    private static Map<String, String> parseQueryStringType(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(pair -> pair.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(keyValue -> decode(keyValue[0]), keyValue -> decode(keyValue[1])));
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static void register(String body, HttpResponse response) {
        Map<String, String> queryString = parseQueryStringType(body);
        String account = queryString.get("account");
        String password = queryString.get("password");
        String email = queryString.get("email");
        if (Objects.isNull(account) || Objects.isNull(password) || Objects.isNull(email)) {
            registerFailure(response, account, password, email);
            return;
        }
        registerSuccess(response, account, password, email);
    }

    private static void registerFailure(HttpResponse response, String account, String password, String email) {
        log.error("account={}, password={}, email={}, 회원가입에 실패하였습니다.", account, password, email);
        response.addHttpStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/404.html");
        response.addHeader("Content-Type", probeContentType("/404.html"));
        response.setBody(findStaticFile("/404.html"));
    }

    private static void registerSuccess(HttpResponse response, String account, String password, String email) {
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("save user: {}", user);
        responseIndexPage(HttpStatus.FOUND, response);
    }
}
