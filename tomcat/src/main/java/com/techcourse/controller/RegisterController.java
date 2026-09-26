package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";

    public HttpResponse handle(final String requestBody) throws IOException {
        if (requestBody != null) {
            createUser(extractQueryParams(requestBody));
        }

        final Path filePath = getFilePath("/register");
        return new HttpResponse(HttpStatus.OK, filePath, getResponseBody(filePath), null, null);
    }

    private void createUser(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");
        final String email = params.get("email");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("registered user: {}", user);
    }

    private Map<String, String> extractQueryParams(final String query) {
        final String[] queryParams = query.split(QUERY_PARAM_DELIMITER);
        final Map<String, String> params = new HashMap<>();

        for (String queryParam : queryParams) {
            final String[] pair = queryParam.split(QUERY_PARAM_VALUE_DELIMITER, 2);
            final String key = pair[0];
            final String value = pair.length == 2 ? pair[1] : "";
            params.put(key, value);
        }
        return params;
    }

    private Path getFilePath(final String uriPath) {
        final String resourceName = "static/" + (uriPath.startsWith("/") ? uriPath.substring(1) : uriPath);
        return resolveResourcePath(resourceName);
    }

    private String getResponseBody(final Path filePath) throws IOException {
        return Files.readString(filePath);
    }

    private Path resolveResourcePath(final String name) {
        final URL url = getClass().getClassLoader().getResource(name);
        if (url == null) {
            return Path.of("/");
        }
        return Path.of(url.getPath());
    }
}
