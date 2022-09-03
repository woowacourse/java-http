package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.util.FileReader;

public class HttpResponse {
    private final HttpResponseStatusLine statusLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(HttpResponseStatusLine statusLine, Map<String, String> headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse ok(String uri, String resource) {
        HttpResponseStatusLine statusLine = new HttpResponseStatusLine(HttpStatus.OK);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", ContentType.findContentType(uri));
        headers.put("Content-Length", String.valueOf(resource.getBytes().length));

        return new HttpResponse(statusLine, headers, resource);
    }

    public static HttpResponse found(String uri) {
        HttpResponseStatusLine statusLine = new HttpResponseStatusLine(HttpStatus.FOUND);

        Map<String, String> headers = new HashMap<>();
        headers.put("Location", uri);

        return new HttpResponse(statusLine, headers, "");
    }

    public static HttpResponse notFound() {
        HttpResponseStatusLine statusLine = new HttpResponseStatusLine(HttpStatus.NOT_FOUND);
        String body = FileReader.read("/404.html");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", ContentType.findContentType("/404.html"));
        headers.put("Content-Length", String.valueOf(body.getBytes().length));

        return new HttpResponse(statusLine, headers, body);
    }

    public String toResponseString() {
        StringBuilder response = new StringBuilder();

        response.append(statusLine.toResponseString() + " \r\n");

        for (String key : headers.keySet()) {
            response.append(key + ": " + headers.get(key) + "\r\n");
        }

        response.append("\r\n");
        response.append(body);

        return response.toString();
    }
}
