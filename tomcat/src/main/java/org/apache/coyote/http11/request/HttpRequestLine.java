package org.apache.coyote.http11.request;

public class HttpRequestLine {

    private String httpMethod;
    private String urlPath;
    private String httpVersion;

    public HttpRequestLine(String httpMethod, String urlPath, String httpVersion) {
        this.httpMethod = httpMethod;
        this.urlPath = urlPath;
        this.httpVersion = httpVersion;
    }

    public boolean matchesMethod(String method) {
        return this.httpMethod.equals(method);
    }

    public boolean matchesFileExtension(String fileExtension) {
        return urlPath.endsWith(fileExtension);
    }

    public String getUrlPath() {
        return urlPath;
    }
}
