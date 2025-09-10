package org.apache.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.http.HttpCookie;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public boolean isProcessable(HttpRequest httpRequest) {
        return httpRequest.pathEquals("/login");
    }

    @Override
    public Map<String, Object> process(HttpRequest httpRequest) throws URISyntaxException, IOException {

        return login(httpRequest);
    }

    private Map<String, Object> login(HttpRequest httpRequest) throws URISyntaxException, IOException {

        // 로그인된 상태에서 로그인 페이지에 접근하면 index 페이지로 리다이렉트
        if (httpRequest.getMethod().equals("GET")) {
            if (httpRequest.containsCookie()) {
                HttpCookie httpCookie = httpRequest.getHttpCookie();

                String jsessionID = httpCookie.getJSessionId();
                Session session = sessionManager.findSession(jsessionID);

                if (session != null && session.getAttribute("user") != null) {
                    return makeResponseBody(" ", HttpStatus.FOUND, httpCookie);
                }
            }

            return makeResponseBody("/login", HttpStatus.OK, null);
        }

        String account = httpRequest.getBodyAttribute("account");
        String password = httpRequest.getBodyAttribute("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);

        if (!user.checkPassword(password)) {
            return makeResponseBody("/login", HttpStatus.UNAUTHORIZED, null);
        }

        if (user.checkPassword(password)) {
            HttpCookie cookie = new HttpCookie();

            String sessionId = UUID.randomUUID().toString();

            Session session = new Session(sessionId);
            session.setAttribute("user", user);

            sessionManager.add(session);

            cookie.setjSessionId(sessionId);
            log.info("user: {}", user);

            return makeResponseBody(" ", HttpStatus.FOUND, cookie);
        }

        return makeResponseBody("/500.html", HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    private Map<String, String> parseQueries(String resource) {
        if (resource.contains("?")) {
            int questionIndex = resource.indexOf("?");

            String queryString = resource.substring(questionIndex + 1);
            String[] queryStrings = queryString.split("&");
            Map<String, String> queryKeyAndValues = new HashMap<>();

            for (String query : queryStrings) {
                String[] queries = query.split("=");
                queryKeyAndValues.put(queries[0], queries[1]);
            }

            return queryKeyAndValues;
        }

        return Map.of();
    }

    private Map<String, Object> makeResponseBody(String resource, HttpStatus httpStatus, HttpCookie httpCookie)
            throws URISyntaxException, IOException {

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", httpStatus);
        responseBody.put("cookie", httpCookie);

        if (httpStatus == HttpStatus.FOUND) {
            responseBody.put("responseBody", "");
            return responseBody;
        }

        String filePath = "";

        if (httpStatus == HttpStatus.UNAUTHORIZED) {
            filePath = "/401.html";
        }

        if (httpStatus == HttpStatus.OK) {
            filePath = parseFilePath(resource);
        }

        final ClassLoader classLoader = getClass().getClassLoader();
        final URL url = classLoader.getResource("static" + filePath);

        if (url == null) {
            throw new IOException("파일이 존재하지 않습니다.");
        }

        final File resourceFile = new File(Objects.requireNonNull(url).toURI());
        final Path path = resourceFile.toPath();

        responseBody.put("responseBody", new String(Files.readAllBytes(path)));

        return responseBody;
    }

    private String parseFilePath(String resource) {
        if (resource.contains("?")) {
            int questionIndex = resource.indexOf("?");
            return resource.substring(0, questionIndex) + ".html";
        }

        return resource + ".html";
    }
}
