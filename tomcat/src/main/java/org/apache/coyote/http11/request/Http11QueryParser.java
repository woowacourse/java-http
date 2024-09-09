package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

class Http11QueryParser {

    List<Http11Query> parse(String requestUri) {
        var rawQueries = parseToMap(requestUri);
        return rawQueries.entrySet().stream()
                .map(entry -> new Http11Query(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private LinkedHashMap<String, String> parseToMap(String requestUri) {
        if (notHasQueryString(requestUri)) {
            return new LinkedHashMap<>();
        }

        String queryString = requestUri.substring(requestUri.indexOf("?") + 1);
        if (isInValidQueryString(queryString)) {
            return new LinkedHashMap<>();
        }

        return Arrays.stream(queryString.split("&"))
                .map(query -> query.split("="))
                .filter(keyAndValue -> keyAndValue.length == 2)
                .collect(Collectors.toMap(
                        keyAndValue -> keyAndValue[0],
                        keyAndValue -> keyAndValue[1],
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    private boolean notHasQueryString(String requestUri) {
        return !requestUri.contains("?");
    }

    private boolean isInValidQueryString(String queryString) {
        return !queryString.contains("=");
    }
}
