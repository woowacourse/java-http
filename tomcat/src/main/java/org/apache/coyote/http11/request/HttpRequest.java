package org.apache.coyote.http11.request;

import org.apache.coyote.http11.FileReader;

public class HttpRequest {

    private final HttpMethod httpMethod;

    private final HttpRequestPath httpRequestPath;

    private final QueryString queryString;

    private final HttpRequestHeaders httpRequestHeaders;

    public HttpRequest(HttpMethod httpMethod,
                       HttpRequestPath httpRequestPath,
                       QueryString queryString,
                       HttpRequestHeaders httpRequestHeaders) {
        this.httpMethod = httpMethod;
        this.httpRequestPath = httpRequestPath;
        this.queryString = queryString;
        this.httpRequestHeaders = httpRequestHeaders;
    }

    public String getHttpRequestPath() {
        return httpRequestPath.uri();
    }

    public String getContentType() {
        try {
            return httpRequestHeaders.getContentType();
        } catch (NullPointerException e) {
            return getContentTypeByFilePath(getHttpRequestPath());
        }
    }

    private String getContentTypeByFilePath(String path) {
        FileReader fileReader = FileReader.getInstance();
        String fileExtension = fileReader.getFileExtension(path);
        if (fileExtension.equals(".js")){
            return "application/javascript";
        }
        if (fileExtension.equals(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    public String getQueryParameter(String key) {
        return queryString.getValue(key);
    }
}
