package org.apache.coyote.support;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final Method method;
    private final String uri;
    private final Map<String, String> parameters;

    private HttpRequestHandler(Method method, String uri, Map<String, String> parameters) {
        this.method = method;
        this.uri = uri;
        this.parameters = parameters;
    }

    private HttpRequestHandler(Method method, String uri) {
        this(method, uri, new HashMap<>());
    }

    public static HttpRequestHandler of(List<String> request) {
        final var startLine = request.get(0).split(" ");
        final var method = Method.valueOf(startLine[0]);
        final var uri = startLine[1];
        if (uri.contains("?")) {
            final var delimiterIndex = uri.indexOf("?");
            final var path = uri.substring(0, delimiterIndex);
            final var queryString = uri.substring(delimiterIndex + 1);
            return new HttpRequestHandler(method, path, parseQueryString(queryString));
        }
        return new HttpRequestHandler(method, uri);
    }

    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> parameters = new HashMap<>();
        for (String param : queryString.split("&")) {
            final var delimiterIndex = param.indexOf("=");
            if (delimiterIndex == -1) {
                continue;
            }
            final var key = param.substring(0, delimiterIndex);
            final var value = param.substring(delimiterIndex + 1);
            parameters.put(key, value);
        }
        return parameters;
    }

    public String handle() {
        if (method.equals(Method.GET)) {
            checkLoginAccount();
            return get();
        }
        throw new UnsupportedOperationException("Not implemented");
    }

    private String get() {
        try {
            return new ResourceResponse(uri).toHttpResponseMessage();
        } catch (NotFoundException e) {
            return ResourceResponse.ofNotFound().toHttpResponseMessage();
        }
    }

    private void checkLoginAccount() {
        if (uri.startsWith("/login") && parameters.containsKey("account")) {
            Optional<User> account = InMemoryUserRepository.findByAccount(parameters.get("account"));
            if (account.isEmpty()) {
                return;
            }
            log.info(account.get().toString());
        }
    }
}
