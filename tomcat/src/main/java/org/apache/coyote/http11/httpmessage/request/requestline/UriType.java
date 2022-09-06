package org.apache.coyote.http11.httpmessage.request.requestline;

public enum UriType {
    FILE_REQUEST,
    PATH_REQUEST,
    HAS_QUERYSTRING;

    public static UriType findByResourcePath(String resourcePath) {
        if (resourcePath.contains(".")) {
            return FILE_REQUEST;
        }
        if (resourcePath.contains("?")) {
            return HAS_QUERYSTRING;
        }
        return PATH_REQUEST;
    }
}
