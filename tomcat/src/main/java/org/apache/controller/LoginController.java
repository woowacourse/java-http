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
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public boolean isProcessable(final String path) {
        return path.contains("/login");
    }

    @Override
    public Map<String, Object> process(final Map<String, String> requests) throws URISyntaxException, IOException {

        return login(requests);
    }

    private Map<String, Object> login(final Map<String, String> requests) throws URISyntaxException, IOException {

        // 로그인된 상태에서 로그인 페이지에 접근하면 index 페이지로 리다이렉트
        if (requests.get("Method").equals("GET")) {
            if (requests.containsKey("Cookie")) {
                HttpCookie httpCookie = new HttpCookie();
                httpCookie.parseCookie(requests.get("Cookie"));

                String jsessionID = httpCookie.getJSessionId();
                Session session = sessionManager.findSession(jsessionID);

                if (session != null && session.getAttribute("user") != null) {
                    return makeResponseBody(" ", HttpStatus.FOUND, httpCookie);
                }
            }

            return makeResponseBody("/login", HttpStatus.OK, null);
        }

        String account = requests.get("account");
        String password = requests.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);

        if (!user.checkPassword(password)) {
            return makeResponseBody("/login", HttpStatus.UNAUTHORIZED, null);
        }

        if (user.checkPassword(password)) { // 로그인 성공 시,
            HttpCookie cookie = new HttpCookie();

            // UUID를 id로 세션을 만들고
            String sessionId = UUID.randomUUID().toString();

            // 유저 정보 넣고
            Session session = new Session(sessionId);
            session.setAttribute("user", user);

            // 세션 매니저에 넣음
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
