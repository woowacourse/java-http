package org.apache.catalina.engine;

import static org.apache.coyote.http11.RequestLine.HTTP_METHOD;
import static org.apache.coyote.http11.RequestLine.REQUEST_URI;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class CatalinaServletEngine {

    private static final Logger log = LoggerFactory.getLogger(CatalinaServletEngine.class);

    public static void processRequest(Map<RequestLine, String> requestLine, String body, HttpResponse response) {
        if (requestLine.get(REQUEST_URI).equals("/")) {
            String contentType = probeContentType("/index.html");
            String content = findStaticFile("/index.html");
            response.addHttpStatus(HttpStatus.OK);
            response.addHeader("Content-Type", contentType);
            response.setBody(content);
            return;
        }
        if (requestLine.get(REQUEST_URI).equals("/login") && requestLine.get(HTTP_METHOD).equals("GET")) {
            String contentType = probeContentType("/login.html");
            String content = findStaticFile("/login.html");
            response.addHttpStatus(HttpStatus.OK);
            response.addHeader("Content-Type", contentType);
            response.setBody(content);
            return;
        }
        if (requestLine.get(REQUEST_URI).equals("/login") && requestLine.get(HTTP_METHOD).equals("POST")) {
            responseLogin(body, response);
            return;
        }
        if (requestLine.get(REQUEST_URI).equals("/register") && requestLine.get(HTTP_METHOD).equals("GET")) {
            String contentType = probeContentType("/register.html");
            String content = findStaticFile("/register.html");
            response.addHttpStatus(HttpStatus.OK);
            response.addHeader("Content-Type", contentType);
            response.setBody(content);
            return;
        }
        if (requestLine.get(REQUEST_URI).equals("/register") && requestLine.get(HTTP_METHOD).equals("POST")) {
            responseRegister(body, response);
            return;
        }
        String content = findStaticFile(requestLine.get(REQUEST_URI));
        if (!StringUtils.isEmpty(content)) {
            String contentType = probeContentType(requestLine.get(REQUEST_URI));
            response.addHttpStatus(HttpStatus.OK);
            response.addHeader("Content-Type", contentType);
            response.setBody(content);
        }
    }

    private static String probeContentType(String url) {
        try {
            return Files.probeContentType(Paths.get(Objects.requireNonNull(CatalinaServletEngine.class.getClassLoader()
                    .getResource("static" + url)).toURI()));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String findStaticFile(String url) {
        try {
            URL resource = CatalinaServletEngine.class.getClassLoader().getResource("static" + url);
            if (Objects.isNull(resource)) {
                return StringUtils.EMPTY;
            }
            return new String(Files.readAllBytes(new File((resource).getFile()).toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void responseLogin(String body, HttpResponse response) {
        try {
            Map<String, String> queryString = parseQueryStringType(body);
            login(queryString, response);
        } catch (RuntimeException e) {
            response.addHttpStatus(HttpStatus.FOUND);
            response.addHeader("Location", "/404.html");
            response.addHeader("Content-Type", probeContentType("/404.html"));
            response.setBody(findStaticFile("/404.html"));
        }
    }

    private static void login(Map<String, String> queryString, HttpResponse response) {
        String account = queryString.get("account");
        String password = queryString.get("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty()) {
            log.error("inputAccount={}, 해당하는 사용자를 찾을 수 없습니다.", account);
            response.addHttpStatus(HttpStatus.FOUND);
            response.addHeader("Location", "/404.html");
            response.addHeader("Content-Type", probeContentType("/404.html"));
            response.setBody(findStaticFile("/404.html"));
            return;
        }
        User user = optionalUser.get();
        if (user.checkPassword(password)) {
            log.info("user: {}", user);
            response.addHttpStatus(HttpStatus.FOUND);
            response.addHeader("Location", "/index.html");
            response.addHeader("Content-Type", probeContentType("/index.html"));
            response.setBody(findStaticFile("/index.html"));
            return;
        }
        log.error("user: {}, inputPassword={}, 비밀번호가 올바르지 않습니다.", user, password);
        response.addHttpStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/401.html");
        response.addHeader("Content-Type", probeContentType("/401.html"));
        response.setBody(findStaticFile("/401.html"));
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

    private static void responseRegister(String body, HttpResponse response) {
        try {
            Map<String, String> queryString = parseQueryStringType(body);
            register(queryString, response);
        } catch (RuntimeException e) {
            response.addHttpStatus(HttpStatus.FOUND);
            response.addHeader("Location", "/404.html");
            response.addHeader("Content-Type", probeContentType("/404.html"));
            response.setBody(findStaticFile("/404.html"));
        }
    }

    private static void register(Map<String, String> queryString, HttpResponse response) {
        String account = queryString.get("account");
        String password = queryString.get("password");
        String email = queryString.get("email");
        if (Objects.isNull(account) || Objects.isNull(password) || Objects.isNull(email)) {
            log.error("account={}, password={}, email={}, 회원가입에 실패하였습니다.", account, password, email);
            response.addHttpStatus(HttpStatus.FOUND);
            response.addHeader("Location", "/404.html");
            response.addHeader("Content-Type", probeContentType("/404.html"));
            response.setBody(findStaticFile("/404.html"));
            return;
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("save user: {}", user);
        response.addHttpStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
        response.addHeader("Content-Type", probeContentType("/index.html"));
        response.setBody(findStaticFile("/index.html"));
    }
}
