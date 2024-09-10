package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;

public class HttpRequestLine {

    private HttpMethod httpMethod;
    private String urlPath;
    private String httpVersion;

    public HttpRequestLine(HttpMethod httpMethod, String urlPath, String httpVersion) {
        this.httpMethod = httpMethod;
        this.urlPath = urlPath;
        this.httpVersion = httpVersion;
    }

    public boolean matchesMethod(HttpMethod other) {
        return this.httpMethod == other;
    }

    public boolean matchesFileExtension(String fileExtension) {
        return urlPath.endsWith(fileExtension);
    }

    public String getUrlPath() {
        return urlPath;
    }
}
