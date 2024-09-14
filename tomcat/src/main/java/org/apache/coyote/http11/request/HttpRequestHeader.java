package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;

public class HttpRequestHeader {
    private final Map<String, String> cookies;
    private final Map<String, List<String>> queryParams;
    private final String fileType;


    public HttpRequestHeader(Map<String, String> cookies, Map<String, List<String>> queryParams, String fileType) {
        this.cookies = cookies;
        this.queryParams = queryParams;
        this.fileType = fileType;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public String getFileType() {
        return fileType;
    }

    @Override
    public String toString() {
        return "HttpRequestHeader{" +
                "cookies=" + cookies +
                ", queryParams=" + queryParams +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
