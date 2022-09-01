package org.apache.coyote.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class HttpRequest {

    private final Method method;
    private final String uri;
    private final Map<String, String> parameters;

    private HttpRequest(Method method, String uri, Map<String, String> parameters) {
        this.method = method;
        this.uri = uri;
        this.parameters = parameters;
    }

    private HttpRequest(Method method, String uri) {
        this(method, uri, new HashMap<>());
    }

    public static HttpRequest of(List<String> request) {
        final var startLine = request.get(0).split(" ");
        final var method = Method.valueOf(startLine[0]);
        final var uri = startLine[1];
        if (uri.contains("?")) {
            final var delimiterIndex = uri.indexOf("?");
            final var path = uri.substring(0, delimiterIndex);
            final var queryString = uri.substring(delimiterIndex + 1);
            return new HttpRequest(method, path, parseQueryString(queryString));
        }
        return new HttpRequest(method, uri);
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

    public boolean isGet() {
        return this.method.equals(Method.GET);
    }

    public Optional<User> checkLoginAccount() {
        if (uri.startsWith("/login") && parameters.containsKey("account")) {
            return InMemoryUserRepository.findByAccount(parameters.get("account"));
        }
        return Optional.empty();
    }

    public String getUri() {
        return uri;
    }
}
