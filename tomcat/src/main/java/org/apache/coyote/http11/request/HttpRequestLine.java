package org.apache.coyote.http11.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestLine {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestLine.class);

    private String httpMethod;
    private String urlPath;
    private String httpVersion;

    public HttpRequestLine(String httpMethod, String urlPath, String httpVersion) {
        this.httpMethod = httpMethod;
        this.urlPath = urlPath;
        this.httpVersion = httpVersion;
        //log.info("urlPath = {}", urlPath);
    }

    public boolean matchesUrlPath(String urlPath) {
        return this.urlPath.equals(urlPath);
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

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }
}
