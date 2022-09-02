package org.apache.coyote.http11;

import static java.util.stream.Collectors.*;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class QueryParams {

    private final Map<String, List<String>> queryParams;

    public QueryParams(URI requestUri) {
        this.queryParams = parseQueryParams(requestUri);
    }

    private Map<String, List<String>> parseQueryParams(URI requestUri) {
        if (Objects.isNull(requestUri)) {
            return Collections.emptyMap();
        }

        String query = requestUri.getQuery();
        if (Objects.isNull(query) || query.isEmpty()) {
            return Collections.emptyMap();
        }

        return Arrays.stream(query.split("&"))
            .map(this::splitQueryParameters)
            .collect(Collectors.groupingBy(
                AbstractMap.SimpleImmutableEntry::getKey, HashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    private AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameters(String query) {
        int index = query.indexOf("=");
        String key = query;
        String value = "";
        if (index > 0) {
            key = key.substring(0, index);
        }

        if (index > 0 && query.length() > index + 1) {
            value = query.substring(index + 1);
        }

        return new AbstractMap.SimpleImmutableEntry<>(
            URLDecoder.decode(key, StandardCharsets.UTF_8),
            URLDecoder.decode(value, StandardCharsets.UTF_8)
        );
    }

    public boolean hasQuery() {
        return !queryParams.isEmpty();
    }

    public boolean containsKey(String queryKey) {
        return queryParams.containsKey(queryKey);
    }

    public String getQueryValue(String queryKey) {
        return queryParams.get(queryKey).get(0);
    }
}
