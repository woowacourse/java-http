package org.apache.coyote;

import com.techcourse.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {

    private final ResponseBuilder responseBuilder;
    private final Service service;

    public RequestHandler() {
        this.responseBuilder = new ResponseBuilder();
        this.service = new Service();
    }

    public String handle(final HttpRequest request) throws IOException {
        byte[] responseBody;
        String uri = request.uri();
        if (uri.contains(".")) {
            responseBody = ResourceLoader.get(uri);
            return responseBuilder.build(request.uri(), "200 OK", responseBody, null);
        }

        String path = uri.substring(1);
        Map<String, String> queryParams = null;
        if (uri.contains("?")) {
            queryParams = extractQueryParams(uri);
        }

        return handlePath(path, queryParams);
    }

    private Map<String, String> extractQueryParams(final String uri) {
        Map<String, String> queryParams = new HashMap<>();

        int index = uri.indexOf("?");
        String queryString = uri.substring(index + 1);
        String[] queries = queryString.split("&");

        for (String query : queries) {
            String[] keyValues = query.split("=");
            String key = keyValues[0];
            String value = keyValues[1];
            queryParams.put(key, value);
        }

        return queryParams;
    }

    private String handlePath(final String path, final Map<String, String> queryParams) {
        return responseBuilder.build(path, "", new byte[0], null);
    }
}
