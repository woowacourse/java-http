package org.apache.coyote.http11.request;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestURIFactory {

    private RequestURIFactory() {
    }

    public static RequestURI create(String value) {
        URI uri = createURI(value);
        String path = extractPath(uri.getPath());
        String extension = extractExtension(uri.getPath());
        Map<String, String> queryString = parseQueryString(uri);

        return new RequestURI(uri, path, extension, queryString);
    }

    private static URI createURI(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI: " + value);
        }
    }

    private static String extractPath(String value) {
        int index = value.indexOf('.');
        if (index == -1) {
            return value;
        }
        return value.substring(0, index);

    }

    private static String extractExtension(String path) {
        int index = path.lastIndexOf('.');
        if (index == -1) {
            return "";
        }
        return path.substring(index + 1);
    }

    private static Map<String, String> parseQueryString(URI uri) {
        String query = uri.getQuery();
        if (query == null) {
            return Map.of();
        }

        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }
}
