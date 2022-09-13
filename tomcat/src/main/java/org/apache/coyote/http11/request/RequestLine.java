package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String CHARSET_UTF_8 = "charset=utf-8 ";
    private static final String CSS_EXTENSION = ".css";
    private static final String JS_EXTENSION = ".js";
    private static final String CSS_TYPE = "text/css;";
    private static final String JS_TYPE = "text/js;";
    private static final String HTML_TYPE = "text/html;";

    private String method;
    private String path;
    private String protocolVersion;

    private RequestLine(final String method, final String path, final String protocolVersion) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine of(final String line) {
        String[] splitedLine = line.split(" ");
        String method = splitedLine[0];
        String path = splitedLine[1];
        String protocolVersion = splitedLine[2];
        return new RequestLine(method, path, protocolVersion);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getResource() {
        return path.split("\\?")[0];
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getContentType() {
        if (path.contains(CSS_EXTENSION)) {
            return CONTENT_TYPE + CSS_TYPE + CHARSET_UTF_8;
        }
        if (path.contains(JS_EXTENSION)) {
            return CONTENT_TYPE + JS_TYPE + CHARSET_UTF_8;
        }
        return CONTENT_TYPE + HTML_TYPE + CHARSET_UTF_8;
    }

    public Map<String, String> getQueries() {
        int index = path.indexOf("?");
        String queryString = path.substring(index + 1);
        String[] keyValues = queryString.split("&");

        Map<String, String> queryMap = new HashMap<>();
        for (String keyValue : keyValues) {
            String key = keyValue.split("=")[0];
            String value = keyValue.split("=")[1];
            queryMap.put(key, value);
        }
        return queryMap;
    }
}
