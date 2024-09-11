package org.apache.catalina.controller.http.request;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.catalina.controller.http.HttpVersion;

public class RequestLine {

    private static final String SP = " ";

    private final HttpMethod method;
    private final String path;
    private final HttpVersion version;

    public RequestLine(HttpMethod method, String path, HttpVersion version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public RequestLine(String method, String path, String version) {
        this(HttpMethod.valueOf(method), path, HttpVersion.of(version));
    }

    public String getRequestURI() {
        int queryStringIndex = path.indexOf('?');
        if (queryStringIndex == -1) {
            return path;
        }
        return path.substring(0, queryStringIndex);
    }

    public Map<String, String[]> getQueryParameters() {
        int index = path.lastIndexOf("?");
        if (index == -1) {
            return Map.of();
        }

        String query = path.substring(index + 1);
        String[] params = query.split("&");

        return Arrays.stream(params)
                .collect(toMap(
                        param -> param.split("=", 2)[0],
                        param -> param.split("=", 2)[1].split(","),
                        (oldValue, newValue) -> newValue,
                        LinkedHashMap::new));
    }


    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public HttpVersion getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return method.name() + SP + path + SP + version;
    }
}
