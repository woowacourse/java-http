package org.apache.coyote.request;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import org.apache.coyote.http.HttpMethod;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final String path;
    private final String versionOfProtocol;
    private final QueuryParam queryParams;

    public RequestLine(String headerLines) {
        String[] requestLineEntries = headerLines.split(" ");

        this.httpMethod = HttpMethod.valueOf(requestLineEntries[0]);
        this.queryParams = new QueuryParam(requestLineEntries[1]);
        this.path = mapPath(requestLineEntries[1]);
        this.versionOfProtocol = requestLineEntries[2];
    }


    private String mapPath(String path) {
        if (queryParams.hasQueryParam()) {
            int queryStringIndex = path.indexOf("?");
            return path.substring(0, queryStringIndex);
        }
        return path;
    }

    public boolean isMethod(HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    public String getContentType() {
        try {
            return Files.probeContentType(new File(path).toPath());
        } catch (IOException e) {
            throw new IllegalArgumentException("Not found path");
        }
    }

    public boolean isStaticRequest() {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        return fileNameMap.getContentTypeFor(path) != null;
    }

    public boolean hasQueryParam() {
        return queryParams.hasQueryParam();
    }

    public String getQueryParam(String paramName) {
        return queryParams.getQueryParam(paramName);
    }

    public String getPath() {
        return path;
    }
}
