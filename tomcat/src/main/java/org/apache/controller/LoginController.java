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
import org.apache.http.HttpCookie;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public boolean isProcessable(final String path) {
        return path.contains("/login");
    }

    @Override
    public Map<String, Object> process(final Map<String, String> requests) throws URISyntaxException, IOException {

        return login(requests);
    }

    private Map<String, Object> login(final Map<String, String> requests) throws URISyntaxException, IOException {

        if (requests.get("Method").equals("GET")) {
            return makeResponseBody("/login", HttpStatus.OK, null);
        }

        String account = requests.get("account");
        String password = requests.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);

        if (!user.checkPassword(password)) {
            return makeResponseBody("/login", HttpStatus.UNAUTHORIZED, null);
        }

        if (user.checkPassword(password)) {
            HttpCookie cookie = new HttpCookie();
            cookie.setjSessionId(UUID.randomUUID().toString());
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

        // 302 Found: 리다이렉션이므로 응답 본문(body)이 필요 없음. 즉시 반환.
        if (httpStatus == HttpStatus.FOUND) {
            responseBody.put("responseBody", ""); // 본문을 비워줌
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
