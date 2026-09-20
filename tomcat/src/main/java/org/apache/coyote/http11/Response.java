package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Response {

    private final HttpStatus httpStatus;
    private final String filePath;
    private final Map<String, String> headers;
    private String body;

    public Response(final HttpStatus httpStatus, final String filePath) {
        this.httpStatus = httpStatus;
        this.filePath = filePath;
        this.headers = new LinkedHashMap<>();
    }

    public static Response ok(final String filePath) {
        return new Response(HttpStatus.OK, filePath);
    }

    public static Response found(final String filePath, final String redirect) {
        final Response response = new Response(HttpStatus.FOUND, filePath);
        response.addHeader("Location", redirect);

        return response;
    }

    public static Response permanentRedirect(final String filePath, final String redirect) {
        final Response response = new Response(HttpStatus.PERMANENT_REDIRECT, filePath);
        response.addHeader("Location", redirect);

        return response;
    }

    public static Response unauthorized() {
        final Response response = new Response(HttpStatus.UNAUTHORIZED, "/401.html");
        response.addHeader("Location", "/401.html");

        return response;
    }

    public static Response notFound() {
        final Response response = new Response(HttpStatus.NOT_FOUND, "/404.html");
        response.addHeader("Location", "/404.html");

        return response;
    }

    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }

    public void addBody(final String body) {
        this.body = body;
    }

    public String headerString() {
        return headers.entrySet()
            .stream()
            .map(header -> header.getKey() + ": " + header.getValue() + " ")
            .collect(Collectors.joining("\r\n"));
    }

    public int httpStatusCode() {
        return httpStatus.code();
    }

    public String httpStatusName() {
        return httpStatus.getName();
    }

    public String filePath() {
        return filePath;
    }

    public String body() {
        return body;
    }


}
