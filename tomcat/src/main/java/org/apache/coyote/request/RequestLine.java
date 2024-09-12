package org.apache.coyote.request;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http.HttpMethod;

public class RequestLine {

    public static final String QUERY_INDICATOR = "?";
    public static final String QUERY_COMPONENT_DELIMITER = "&";
    public static final String QUERY_COMPONENT_VALUE_DELIMITER = "=";

    private final HttpMethod httpMethod;
    private final String path;
    private final String versionOfProtocol;
    private final Map<String, String> queryParams;

    public RequestLine(String requestLine) {
        String[] requestLineEntries = requestLine.split(" ");

        this.httpMethod = HttpMethod.valueOf(requestLineEntries[0]);
        this.queryParams = mapQueryParam(requestLineEntries[1]);
        this.path = mapPath(requestLineEntries[1]);
        this.versionOfProtocol = requestLineEntries[2];
    }

    private String mapPath(String path) {
        if (!queryParams.isEmpty()) {
            int queryStringIndex = path.indexOf("?");
            return path.substring(0, queryStringIndex);
        }
        return path;
    }

    public boolean isMethod(HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    public String getPath() {
        return path;
    }

    public boolean hasQueryParam() {
        return !queryParams.isEmpty();
    }

    public String getQueryParam(String paramName) {
        return queryParams.get(paramName);
    }

    private Map<String, String> mapQueryParam(String requestLineEntry) {
        Map<String, String> mappedQueryParams = new HashMap<>();
        if (!requestLineEntry.contains(QUERY_INDICATOR)) {
            return mappedQueryParams;
        }

        int queryParamIndex = requestLineEntry.indexOf(QUERY_INDICATOR);
        String queryString = requestLineEntry.substring(queryParamIndex + 1);
        String[] splittedQueryString = queryString.split(QUERY_COMPONENT_DELIMITER);

        for (String queryParamEntry : splittedQueryString) {
            mappedQueryParams.put(
                    queryParamEntry.split(QUERY_COMPONENT_VALUE_DELIMITER)[0],
                    queryParamEntry.split(QUERY_COMPONENT_VALUE_DELIMITER)[1]
            );
        }
        return mappedQueryParams;
    }

    public String getContentType() throws IOException {
        return Files.probeContentType(new File(path).toPath());
    }

    public boolean isStaticRequest() {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        return fileNameMap.getContentTypeFor(path) != null;
    }

    public boolean isPath(String path) {
        return this.path.equals(path);
    }

    public boolean isPathWithQuery(String path) {
        return isPath(path) && hasQueryParam();
    }
}
