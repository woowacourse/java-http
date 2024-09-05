package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {

    public static Http11Response build(Http11Request request) throws IOException, URISyntaxException {
        String body = buildBody(request);
        Map<String, String> headers = buildHeaders(request, body);
        return new Http11Response(headers, body);
    }

    private static String buildBody(Http11Request request) throws IOException, URISyntaxException {
        String path = request.getPath(); // /login, /index.html, /css/styles ,,,
        if (path.equals("/")) {
            return "Hello world!";
        }
        URL resource = ResponseBuilder.class.getResource("/static/" + buildFilePath(path));
        if (resource == null) {
            return "Hello world!";
        }
        return Files.readString(Paths.get(resource.toURI()));
    }

    private static String buildFilePath(String path) {
        if (!path.contains(".")) {
            return String.format("/static/%s", path + ".html");
        }
        return String.format("/static/%s", path);
    }

    private static Map<String, String> buildHeaders(Http11Request request, String responseBody) {
        Map<String, String> headers = new HashMap<>();
        String contentType = buildContentTypeHeader(request);
        headers.put("Content-Type", String.format("text/%s;charset=utf-8", contentType));
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        return headers;
    }

    private static String buildContentTypeHeader(Http11Request request) {
        Map<String, String> headers = request.getHeaders();
        String accepts = headers.get("Accept");
        return Arrays.stream(accepts.split(","))
                .filter(accept -> accept.startsWith("text/"))
                .findFirst()
                .orElse("text/html")
                .split("/")[1];
    }
}
