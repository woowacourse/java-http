package org.apache.coyote.http11.handler;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.MimeType;

import java.nio.charset.StandardCharsets;

public abstract class HttpRequestHandler {

    abstract String getSupportedUrl();

    public final String handle(String request) {
        validateSupports(request);
        HttpMethod requestMethod = HttpMethod.fromHttp11Request(request);
        if (requestMethod.equals(HttpMethod.GET)) {
            return handleGet(request);
        }
        return handleMethodNotAllow(request);
    }

    protected abstract String handleGet(String request);

    private void validateSupports(String request) {
        String url = getUrl(request);
        if (!supportsUrl(url)) {
            throw new IllegalArgumentException("URL " + url + " not supported.");
        }
    }

    private boolean supportsUrl(String url) {
        return getSupportedUrl().equals(url);
    }

    private String handleMethodNotAllow(String request) {
        HttpMethod requestMethod = HttpMethod.fromHttp11Request(request);
        String responseBody = String.format("Http Method %s not allowed", requestMethod);
        return String.join(
                "\r\n",
                "HTTP/1.1 " + HttpStatus.METHOD_NOT_ALLOWED.getPrase(),
                "Content-Type: " + MimeType.TEXT_PLAIN.getMimeType(),
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                responseBody
        );
    }

    protected String getUrl(String request) {
        return request.split(System.lineSeparator())[0].split(" ")[1].split("\\?")[0];
    }
}
