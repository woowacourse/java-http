package org.apache.coyote.http11.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UriComponentsBuilder {

    private static final Pattern QUERY_STRING_PATTERN = Pattern.compile("([^&=]+)=([^&]*)");
    private final URI uri;
    private final Map<String, List<String>> queryParams;

    private UriComponentsBuilder(URI uri) {
        this.uri = uri;
        this.queryParams = new HashMap<>();
    }

    private UriComponentsBuilder(URI uri, Map<String, List<String>> queryParams) {
        this.uri = uri;
        this.queryParams = queryParams;
    }

    public static UriComponentsBuilder of(String uri) {
        return new UriComponentsBuilder(URI.create(uri));
    }

    public static UriComponentsBuilder of(String path, Map<String, List<String>> queryParams) {
        String queryString = queryParams.entrySet()
                .stream()
                .map(entry -> crateQueryString(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));

        return new UriComponentsBuilder(URI.create(path + "?" + queryString));
    }

    private static String crateQueryString(String key, List<String> values) {
        return values.stream()
                .map(value -> String.format("%s=%s", key, value))
                .collect(Collectors.joining("&"));
    }

    public UriComponentsBuilder build() {
        return new UriComponentsBuilder(uri, createQueryParams());
    }

    private Map<String, List<String>> createQueryParams() {
        HashMap<String, List<String>> result = new HashMap<>();
        String query = uri.getQuery();

        Matcher matcher = QUERY_STRING_PATTERN.matcher(query);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            List<String> values = result.getOrDefault(key, new ArrayList<>());
            values.add(value);

            result.put(key, values);
        }

        return result;
    }

    public String getUri() {
        return uri.toString();
    }

    public String getPath() {
        return uri.getPath();
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }
}
