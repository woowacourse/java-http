package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequestHeader {

    private static final String DEFAULT_INDEX = "/index.html";
    private static final String HTML_EXTENSION = ".html";
    private static final String QUESTION_MARK = "?";
    private static final String EXTENSION = ".";
    private static final String SPACE_DELIMITER = " ";
    private static final String HOME_PATH = "/";

    private final String startLine;
    private final Map<String, String> headers;

    public HttpRequestHeader(String startLine, Map<String, String> headers) {
        this.startLine = startLine;
        this.headers = headers;
    }

    public String getRequestUrlWithOutQuery() {
        String requestUrl = getRequestUrl();
        if (requestUrl.contains(QUESTION_MARK)) {
            return  requestUrl.substring(0, requestUrl.indexOf(QUESTION_MARK));
        }
        return requestUrl;
    }

    public String getQuery() {
        String requestUrl = getRequestUrl();
        int index = requestUrl.indexOf(QUESTION_MARK);
        if (index != -1 && index < requestUrl.length() - 1) {
            return requestUrl.substring(index + 1);
        }
        return "";
    }

    public String getRequestUrl() {
        String requestUrl = startLine.split(SPACE_DELIMITER)[1];
        return checkDefaultRequestUrl(requestUrl);
    }

    private String checkDefaultRequestUrl(String requestUrl) {
        if (requestUrl.equals(HOME_PATH)) {
            return DEFAULT_INDEX;
        }
        if (!requestUrl.contains(EXTENSION)) {
            return addExtension(requestUrl);
        }
        return requestUrl;
    }

    private String addExtension(String requestUrl) {
        int index = requestUrl.indexOf(QUESTION_MARK);
        if (index != -1) {
            String path = requestUrl.substring(0, index);
            String query = requestUrl.substring(index + 1);
            return path + HTML_EXTENSION + QUESTION_MARK + query;
        }
        return requestUrl + HTML_EXTENSION;
    }
}
