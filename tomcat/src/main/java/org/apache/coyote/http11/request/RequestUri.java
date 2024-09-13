package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;

public class RequestUri {
    private static final String ROOT = "/";
    private static final String DOT = ".";
    private static final String DELIMITER = "\\.";
    private static final String DEFAULT_EXTENSION = "html";

    private final String requestUri;

    public RequestUri(String inputRequestUri) {
        this.requestUri = makeToFileUri(inputRequestUri);
    }

    private String makeToFileUri(String uri) {
        if (isRoot(uri) || containsDot(uri)) {
            return uri;
        }
        return uri + DOT + DEFAULT_EXTENSION;
    }

    private boolean isRoot(String uri) {
        return uri.equals(ROOT);
    }

    private boolean containsDot(String uri) {
        return uri.contains(DOT);
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getRequestUrl() {
        List<String> parts = Arrays.asList(requestUri.split(DELIMITER));
        if (parts.size() <= 1) {
            return requestUri;
        }

        return String.join(DOT, parts.subList(0, parts.size() - 1));
    }

    public String getExtension() {
        if (isRoot() || !containsDot()) {
            return DEFAULT_EXTENSION;
        }
        return Arrays.asList(requestUri.split(DELIMITER)).getLast();
    }

    private boolean isRoot() {
        return requestUri.equals(ROOT);
    }

    private boolean containsDot() {
        return requestUri.contains(DOT);
    }
}
