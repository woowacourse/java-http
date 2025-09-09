package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final HttpStatusCode statusCode;
    private final ContentType contentType;
    private final Map<String, String> headers = new HashMap<>();
    private final HttpCookie cookie = new HttpCookie();
    private final String body;

    public HttpResponse(HttpStatusCode statusCode, ContentType contentType, String body) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }

    public String headersToString() {
        if (headers.isEmpty() && cookie.isEmpty()) {
            return null;
        }

        List<String> headerString = new ArrayList<>(headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .toList());
        headerString.add("Set-Cookie: " + cookie.toSetCookieString());
        return String.join("\r\n", headerString);
    }

    public String createString() throws IOException, URISyntaxException {
        String headerString = headersToString();
        String responseBody = bodyToString();
        return buildResponse(headerString, responseBody).toString();
    }

    public void addCookie(String key, String value) {
        cookie.add(key, value);
    }

    public void setLocation(String value) {
        headers.put("Location", value);
    }

    private StringBuilder buildResponse(String headerString, String responseBody) {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ")
                .append(statusCode.getValue()).append(" ")
                .append(statusCode).append(" \r\n");
        if (headerString != null) {
            responseBuilder.append(headerString).append(" \r\n");
        }
        responseBuilder.append("Content-Type: ").append(contentType.get()).append(" \r\n");
        responseBuilder.append("Content-Length: ").append(responseBody.getBytes().length).append(" \r\n\r\n");
        responseBuilder.append(responseBody);
        return responseBuilder;
    }

    private String bodyToString() throws IOException, URISyntaxException {
        URL resource = getResource(body);
        if (resource == null || Files.isDirectory(Path.of(resource.toURI()))) {
            return "Hello world!";
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private URL getResource(String path) {
        return getClass()
                .getClassLoader()
                .getResource("static" + path);
    }
}
