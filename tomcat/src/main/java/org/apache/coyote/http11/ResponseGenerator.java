package org.apache.coyote.http11;

import org.apache.coyote.http11.response.HttpResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class ResponseGenerator {

    private static final String STATIC = "static";
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER_KEY = "Content-Length";
    private static final String SET_COOKIE_HEADER_KEY = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";

    public static String makeResponse(final HttpResponse response) throws IOException {
        if (response.containBody()) {
            return makeHttpResponse(response, response.getBody());
        }
        final var resource = ResponseGenerator.class.getClassLoader().getResource(STATIC + response.getRedirect());
        final String responseBody = makeResponseBody(resource);
        return makeHttpResponse(response, responseBody);
    }

    private static String makeHttpResponse(final HttpResponse response, final String responseBody) {
        final var stringBuilder = new StringBuilder();
        makeBasicResponse(response.getStatusCode(), response.getContentType(),
                responseBody.getBytes().length, stringBuilder);
        addJsessionId(response, stringBuilder);
        addOtherHeader(response, stringBuilder);
        addResponseBody(responseBody, stringBuilder);
        return stringBuilder.toString();
    }

    private static void makeBasicResponse(final StatusCode statusCode, final ContentType contentType,
                                          final int contentLength, final StringBuilder stringBuilder) {
        stringBuilder.append(HTTP_VERSION)
                .append(statusCode.getResponse())
                .append("\r\n");
        stringBuilder.append(CONTENT_TYPE_HEADER_KEY + ": ")
                .append(contentType.getType())
                .append(";charset=utf-8\r\n");
        stringBuilder.append(CONTENT_LENGTH_HEADER_KEY + ": ")
                .append(contentLength)
                .append("\r\n");
    }

    private static void addJsessionId(final HttpResponse response, final StringBuilder stringBuilder) {
        if (response.containJsessionId()) {
            stringBuilder.append(SET_COOKIE_HEADER_KEY + ": ")
                    .append(JSESSIONID + "=")
                    .append(response.findCookie(JSESSIONID))
                    .append("\r\n");
        }
    }

    private static void addOtherHeader(final HttpResponse response, final StringBuilder stringBuilder) {
        if (response.isOtherHeaderNotEmpty()) {
            final String additionalHeaders = response.getOtherHeader()
                    .entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue())
                    .collect(Collectors.joining("\r\n"));
            stringBuilder.append(additionalHeaders).append("\r\n");
        }
    }

    private static void addResponseBody(final String responseBody, final StringBuilder stringBuilder) {
        stringBuilder.append("\r\n");
        stringBuilder.append(responseBody);
    }

    private static String makeResponseBody(final URL resource) throws IOException {
        final var actualFilePath = new File(resource.getPath()).toPath();
        final var fileBytes = Files.readAllBytes(actualFilePath);
        return new String(fileBytes, StandardCharsets.UTF_8);
    }
}
