package org.apache.catalina.request;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.catalina.http.HttpMethod;

public class RequestLine {

    public static final String COMPONENT_DELIMITER = " ";
    public static final String PATH_INDICATOR = "?";
    public static final int METHOD_INDEX = 0;
    public static final int PATH_INDEX = 1;
    public static final int VERSION_OF_PROTOCOL_INDEX = 2;

    private final HttpMethod httpMethod;
    private final String path;
    private final String versionOfProtocol;
    private final QueuyParam queryParams;

    public RequestLine(String headerLines) {
        String[] requestLineEntries = headerLines.split(COMPONENT_DELIMITER);

        this.httpMethod = HttpMethod.valueOf(requestLineEntries[METHOD_INDEX]);
        this.queryParams = new QueuyParam(requestLineEntries[PATH_INDEX]);
        this.path = mapPath(requestLineEntries[PATH_INDEX]);
        this.versionOfProtocol = requestLineEntries[VERSION_OF_PROTOCOL_INDEX];
    }


    private String mapPath(String path) {
        if (queryParams.hasQueryParam()) {
            int queryStringIndex = path.indexOf(PATH_INDICATOR);
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
        try {
            return Files.probeContentType(Paths.get(path)) != null;
        } catch (IOException e) {
            return false;
        }
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
