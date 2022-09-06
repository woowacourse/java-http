package org.apache.coyote.http11;

import static java.util.stream.Collectors.*;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class QueryParams {

    private final Map<String, String> queryParams = new HashMap<>();

    QueryParams(URI requestUri) {
        parseQueryParams(requestUri);
    }

    private void parseQueryParams(URI requestUri) {
        if (Objects.isNull(requestUri)) {
            return;
        }

        final String query = requestUri.getQuery();
        if (Objects.isNull(query) || query.isEmpty()) {
            return;
        }

        splitQueryParameters(query);
    }

    private void splitQueryParameters(String query) {
        final String decodedQuery = URLDecoder.decode(query, StandardCharsets.UTF_8);

        final List<String[]> splitQuery = Arrays.stream(decodedQuery.split("&"))
            .map(it -> it.split("=")).collect(toList());

        for (String[] parameter : splitQuery) {
            queryParams.put(parameter[0], parameter[1]);
        }
    }

    void addQuery(String query) {
        if (query.isEmpty()) {
            return;
        }

        splitQueryParameters(query);
    }

    boolean hasQuery() {
        return !queryParams.isEmpty();
    }

    String getQueryValue(String queryKey) {
        return queryParams.get(queryKey);
    }
}
