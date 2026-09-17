package org.apache.coyote.http11.resolver;

public class PageResolver {

    private static final String HOME_PATH = "/";
    private static final String DEFAULT_INDEX = "/index.html";
    private static final String EXTENSION_DOT = ".";
    private static final String HTML_EXTENSION = ".html";

    public static String resolve(String uri) {
        if (uri.equals(HOME_PATH)) {
            return DEFAULT_INDEX;
        }
        if (!uri.contains(EXTENSION_DOT)) {
            return uri + HTML_EXTENSION;
        }
        return uri;
    }
}
