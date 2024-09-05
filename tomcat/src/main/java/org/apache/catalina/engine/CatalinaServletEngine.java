package org.apache.catalina.engine;

import static org.apache.coyote.http11.RequestLine.REQUEST_URI;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
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

    public static void processRequest(Map<RequestLine, String> requestLine, HttpResponse response) {
        if (requestLine.get(REQUEST_URI).equals("/")) {
            String contentType = probeContentType("/index.html");
            String content = findStaticFile("/index.html");
            response.addHttpStatus(HttpStatus.OK);
            response.addHeader("Content-Type", contentType);
            response.setBody(content);
            return;
        }
        if (requestLine.get(REQUEST_URI).startsWith("/login")) {
            responseLogin(requestLine.get(REQUEST_URI), response);
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

    private static void responseLogin(String uri, HttpResponse response) {
        if (uri.equals("/login")) {
            String content = findStaticFile("/login.html");
            String contentType = probeContentType("/login.html");
            response.addHttpStatus(HttpStatus.OK);
            response.addHeader("Content-Type", contentType);
            response.setBody(content);
            return;
        }
        try {
            Map<String, String> queryString = parseQueryString(uri.substring("/login?".length()));
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

    private static Map<String, String> parseQueryString(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(pair -> pair.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
    }
}
