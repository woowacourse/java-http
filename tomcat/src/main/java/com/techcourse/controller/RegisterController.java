package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
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

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final Path filePath = getFilePath("/register");
        response.set(HttpStatus.OK, filePath, Files.readString(filePath), null, null);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        createUser(extractQueryParams(request.getBody().getContent()));
        response.set(HttpStatus.FOUND, getFilePath("/index.html"), "", "/index.html", null);
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

    private Path resolveResourcePath(final String name) {
        final URL url = getClass().getClassLoader().getResource(name);
        if (url == null) {
            return Path.of("/");
        }
        return Path.of(url.getPath());
    }
}
