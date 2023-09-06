package org.apache.coyote.http11.request;

public class RequestURI {

    private static final String STATIC_PATH = "static";
    private static final String HTML_EXTENSION = ".html";

    private final String uri;
    private final String resourcePath;
    private final QueryString queryString;

    private RequestURI(
            String uri,
            String resourcePath,
            QueryString queryString
    ) {
        this.uri = uri;
        this.resourcePath = resourcePath;
        this.queryString = queryString;
    }

    public static RequestURI from(String uri) {
        return new RequestURI(uri, parseResourcePath(uri), QueryString.from(uri));
    }

    private static String parseResourcePath(String uri) {
        int index = uri.indexOf("?");
        if (index != -1) {
            return STATIC_PATH + uri.substring(0, index) + HTML_EXTENSION;
        }
        if (!uri.contains(".")) {
            return STATIC_PATH + uri + HTML_EXTENSION;
        }
        return STATIC_PATH + uri;
    }

    public boolean hasQueryString() {
        return !queryString.getValues().isEmpty();
    }

    public boolean isLoginPage() {
        return uri.equals("/login");
    }

    public boolean isHome() {
        return uri.equals("/");
    }

    public boolean isRegister() {
        return uri.contains("/register");
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public QueryString getQueryString() {
        return queryString;
    }

    public String getUri() {
        return uri;
    }

}
