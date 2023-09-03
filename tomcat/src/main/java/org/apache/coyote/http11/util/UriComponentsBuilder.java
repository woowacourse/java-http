package org.apache.coyote.http11.util;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UriComponentsBuilder {

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
        
        return new UriComponentsBuilder(URI.create(path + "?" + QueryStringParser.parse(queryParams)));
    }

    public UriComponentsBuilder build() {
        String query = uri.getQuery();

        return new UriComponentsBuilder(uri, QueryStringParser.parse(query));
    }

    public String getUri() {
        return uri.toString();
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }
}
