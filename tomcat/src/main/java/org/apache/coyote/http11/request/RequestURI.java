package org.apache.coyote.http11.request;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestURI {

    private final URI uri; // /index.html?name=John
    private final String path; // /index
    private final String extension; // html
    private final Map<String, String> queryString; // name=John

    public RequestURI(String value) {
        this.uri = createURI(value);
        this.path = extractPath(uri.getPath());
        this.extension = extractExtension(uri.getPath());
        this.queryString = parseQueryString(uri);
    }

    public String getURI() {
        return uri.toString();
    }

    public String getExtension() {
        return extension;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    private URI createURI(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI: " + value);
        }
    }

    private String extractPath(String value) {
        int index = value.indexOf('.');
        if (index == -1) {
            return value;
        }
        return value.substring(0, index);

    }

    private String extractExtension(String path) {
        int index = path.lastIndexOf('.');
        if (index == -1) {
            return "html"; // default extension = html
        }
        return path.substring(index + 1);
    }

    private Map<String, String> parseQueryString(URI uri) {
        String query = uri.getQuery();
        if (query == null) {
            return Map.of();
        }

        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }
}
