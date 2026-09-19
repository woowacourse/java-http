package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestTarget {

    private static final String LOGIN_PATH = "/login";

    private final String path;
    private final Map<String, String> queryParameters;

    public RequestTarget(String value) {
        int queryStart = value.indexOf("?");
        if (queryStart == -1) {
            this.path = value;
            this.queryParameters = Map.of();
            return;
        }

        this.path = value.substring(0, queryStart);
        String queryString = value.substring(queryStart + 1);
        this.queryParameters = Map.copyOf(parseQueryParameters(queryString));
    }

    private Map<String, String> parseQueryParameters(String queryString) {
        Map<String, String> queryParameters = new HashMap<>();
        for (String queryParameter : queryString.split("&")) {
            String[] pair = queryParameter.split("=", 2);
            if (pair.length == 2) {
                queryParameters.put(pair[0], pair[1]);
            }
        }
        return queryParameters;
    }

    public boolean isLogin() {
        return path.equals(LOGIN_PATH);
    }

    public String resourcePath() {
        if (isLogin()) {
            return "/login.html";
        }
        return path;
    }

    public Optional<String> queryParameter(String name) {
        return Optional.ofNullable(queryParameters.get(name));
    }

    public String extension() {
        int extensionStart = path.lastIndexOf(".");
        if (extensionStart < path.lastIndexOf("/")) {
            return "";
        }
        return path.substring(extensionStart + 1);
    }
}
