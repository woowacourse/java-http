package org.apache.coyote;

import com.techcourse.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandler {

    private final ResponseBuilder responseBuilder;
    private final Service service;
    private final List<String> views = List.of("login", "register");

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

        if (views.contains(path) && queryParams == null && request.body() == null) {
            responseBody = ResourceLoader.get(uri + ".html");
            return responseBuilder.build(uri + ".html", "200 OK", responseBody, null);
        }

        if (request.method().equals("GET")) {
            return handleGet(path, queryParams);
        }

        if (request.method().equals("POST")) {
            return handlePost(path, request.body());
        }

        return null;
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

    private String handleGet(final String path, final Map<String, String> queryParams) {
        if (path.startsWith("login")) {
            final var responseBody = service.findUser(queryParams);
            final int index = path.indexOf("?");
            final String filePath = path.substring(0, index);
            final Map<String, String> headers = new HashMap<>();
            headers.put("Location", "/index.html");
            return responseBuilder.build(filePath + ".html", "302 Found", responseBody, headers);
        }

        return responseBuilder.build(path, "", new byte[0], null);
    }

    private String handlePost(final String path, final String body) {
        if (path.startsWith("register")) {
            Map<String, String> map = new HashMap<>();
            for (String keyValue : body.split("&")) {
                int index = keyValue.indexOf("=");
                String key = keyValue.substring(0, index);
                String value = keyValue.substring(index);
                map.put(key, value);
            }
            service.registerUser(map.get("account"), map.get("password"), map.get("email"));
            final Map<String, String> headers = new HashMap<>();
            headers.put("Location", "/index.html");
            return responseBuilder.build(path + ".html", "302 Found", new byte[0], headers);
        }

        return responseBuilder.build(path, "", new byte[0], null);
    }
}
