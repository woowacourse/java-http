package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.util.StringUtils.SPACE;

public class RequestPath {

    private static final int RESOURCE_INDEX = 1;
    private static final String JS_FILE_EXTENSION = ".js";
    private static final String HTML_FILE_EXTENSION = ".html";
    private static final String CSS_FILE_EXTENSION = ".css";
    private static final String ROOT_PAGE_REQUEST_URI = "/";
    private static final String LOGIN_REQUEST = "/login";
    private static final String REGISTER_REQUEST = "/register";
    private static final String QUERY_STRING_PREFIX = "?";
    private static final String REQUEST_FILE_PATH_QUERY_STRING_DELIMITER = "\\?";
    private static final int REQUEST_PATH_INDEX = 0;

    private final String value;

    public RequestPath(String value) {
        this.value = value;
    }

    public static RequestPath from(String requestLine) {
        String requestPath = requestLine.split(SPACE)[RESOURCE_INDEX];
        if (requestLine.contains(QUERY_STRING_PREFIX)) {
            return new RequestPath(
                    requestPath.split(REQUEST_FILE_PATH_QUERY_STRING_DELIMITER)[REQUEST_PATH_INDEX]);
        }
        return new RequestPath(requestPath);
    }

    public String getValue() {
        return value;
    }

    public boolean containsJsFileExtension() {
        return value.contains(JS_FILE_EXTENSION);
    }

    public boolean containsCssFileExtension() {
        return value.contains(CSS_FILE_EXTENSION);
    }

    public boolean containsHtmlFileExtension() {
        return value.contains(HTML_FILE_EXTENSION);
    }

    public boolean containsLoginPath() {
        return value.equals(LOGIN_REQUEST);
    }

    public boolean isRootPath() {
        return value.equals(ROOT_PAGE_REQUEST_URI);
    }

    public boolean containsRegisterPath() {
        return value.equals(REGISTER_REQUEST);
    }
}
