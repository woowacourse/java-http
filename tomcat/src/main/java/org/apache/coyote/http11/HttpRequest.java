package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private String method;
    private String path;
    private String protocol;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> queryParameters = new HashMap<>();

    public HttpRequest(List<String> request) {
        String[] split = request.getFirst().split(" ");
        method = split[0];
        protocol = split[2];
        parseUri(split[1]);
        parseHeaders(request.subList(1, request.size() - 1));
    }

    private void parseUri(String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            path = uri;
            return;
        }
        path = uri.substring(0, index);
        parseQueryString(uri.substring(index + 1));
    }

    private void parseQueryString(String queryString) {
        String[] parameters = queryString.split("&");

        Arrays.stream(parameters)
                .forEach(parameter -> {
                            String[] split = parameter.split("=");
                            queryParameters.put(split[0], split[1]);
                        }
                );
    }

    private void parseHeaders(List<String> headerString) {
        if (headerString.isEmpty()) {
            return;
        }

        headerString.forEach(
                header -> {
                    int index = header.indexOf(": ");
                    headers.put(
                            header.substring(0, index),
                            header.substring(index + 1)
                    );
                }
        );
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(String key) {
        return queryParameters.get(key);
    }
}
