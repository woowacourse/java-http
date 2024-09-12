package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;

public class HttpRequestLine {

    private static final String FILE_EXTENSION_DELIMITER = ".";
    private static final String EMPTY_STRING = "";

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

    public String getUrlPath() {
        return urlPath;
    }

    public String getFileExtension() {
        int fileExtensionIndex = urlPath.lastIndexOf(FILE_EXTENSION_DELIMITER);
        if (existsFileExtension()) {
            return urlPath.substring(fileExtensionIndex + 1);
        }
        return EMPTY_STRING;
    }

    private boolean existsFileExtension() {
        return urlPath.lastIndexOf(FILE_EXTENSION_DELIMITER) >= 0;
    }
}
