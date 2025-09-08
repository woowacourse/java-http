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
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    Map<String, String> queries = new HashMap<>();

    @Override
    public boolean isProcessable(final String path) {
        return path.contains("/login");
    }

    @Override
    public Map<String, Object> process(final Map<String, String> requests) throws URISyntaxException, IOException {
        final String path = requests.get("Path");
        queries = parseQueries(path);

        return login(requests);
    }

    private Map<String, Object> login(final Map<String, String> requests) throws URISyntaxException, IOException {
        HttpStatus httpStatus = HttpStatus.OK;
        String path = requests.get("Path");

        Map<String, String> queries = parseQueries(path);

        if (!queries.isEmpty()) {
            String account = queries.get("account");
            String password = queries.get("password");

            try {
                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(IllegalArgumentException::new);

                if (!user.checkPassword(password)) {
                    httpStatus = HttpStatus.UNAUTHORIZED;
                }

                if (user.checkPassword(password)) {
                    log.info("user: {}", user);
                    httpStatus = HttpStatus.FOUND;
                }

            } catch (Exception e) {
                httpStatus = HttpStatus.UNAUTHORIZED;
            }
        }

        return makeResponseBody(path, httpStatus);
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

    private Map<String, Object> makeResponseBody(String resource, HttpStatus httpStatus)
            throws URISyntaxException, IOException {

        Map<String, Object> responseBody = new HashMap<>();
        String filePath = "";

        if (httpStatus == HttpStatus.UNAUTHORIZED) {
            filePath = "/401.html";
        }

        if (httpStatus == HttpStatus.FOUND) {
            filePath = "/index.html";
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
        responseBody.put("status", httpStatus);

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
