package org.apache.coyote.http11.request;

import org.apache.coyote.http11.FileReader;

public class HttpRequest {

    private final HttpMethod httpMethod;

    private final QueryString queryString;

    private final HttpRequestHeaders httpRequestHeaders;

    private final HttpRequestBody httpRequestBody;

    private HttpRequestPath httpRequestPath;

    public HttpRequest(HttpMethod httpMethod,
                       HttpRequestPath httpRequestPath,
                       QueryString queryString,
                       HttpRequestHeaders httpRequestHeaders,
                       HttpRequestBody httpRequestBody) {
        this.httpMethod = httpMethod;
        this.httpRequestPath = httpRequestPath;
        this.queryString = queryString;
        this.httpRequestHeaders = httpRequestHeaders;
        this.httpRequestBody = httpRequestBody;
    }

    public void setHttpRequestPath(String filePath) {
        this.httpRequestPath = new HttpRequestPath(filePath);
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
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

    public String getRequestBodyValue(String key) {
        return httpRequestBody.getValue(key);
    }

    public boolean isParameterEmpty() {
        return queryString.isEmpty();
    }
}
