package org.apache.coyote.http11.httpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record Params(Map<String, String> params) {

    public static Params parse(final String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return new Params(Map.of());
        }

        final String[] queryStrings = queryString.split("&");
        final Map<String, String> params = new HashMap<>();

        for (String param : queryStrings) {
            final String name = param.split("=")[0];
            final String value = param.split("=")[1];
            params.put(name, value);
        }

        return new Params(params);
    }

    public static Params parseFromCookie(final String cookieOfRequest) {
        if (cookieOfRequest == null || cookieOfRequest.isBlank()) {
            return new Params(Map.of());
        }

        final String[] cookiesOfRequest = cookieOfRequest.split("; ");
        final Map<String, String> cookies = new HashMap<>();

        for (String cookie : cookiesOfRequest) {
            cookie = cookie.trim();
            String[] tokens = cookie.split("=", 2);

            if (tokens.length < 2) {
                continue;
            }

            final String name = tokens[0];
            final String value = tokens[1];
            cookies.put(name, value);
        }

        return new Params(cookies);
    }

    public static Params empty() {
        return new Params(Map.of());
    }

    public Optional<String> findValue(final String name) {
        return Optional.ofNullable(params.get(name));
    }
}
