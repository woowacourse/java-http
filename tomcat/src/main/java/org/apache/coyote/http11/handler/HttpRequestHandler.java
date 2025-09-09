package org.apache.coyote.http11.handler;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Map;

public abstract class HttpRequestHandler {

    abstract String getSupportedUrl();

    public final HttpResponse handle(String request) {
        validateSupports(request);
        HttpMethod requestMethod = HttpMethod.fromHttp11Request(request);
        if (requestMethod.equals(HttpMethod.GET)) {
            return handleGet(request);
        }
        return handleMethodNotAllow(request);
    }

    protected abstract HttpResponse handleGet(String request);

    private void validateSupports(String request) {
        String url = getUrl(request);
        if (!supportsUrl(url)) {
            throw new IllegalArgumentException("URL " + url + " not supported.");
        }
    }

    private boolean supportsUrl(String url) {
        return getSupportedUrl().equals(url);
    }

    private HttpResponse handleMethodNotAllow(String request) {
        HttpMethod requestMethod = HttpMethod.fromHttp11Request(request);
        return new HttpResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                String.format("Http Method %s not allowed", requestMethod),
                MimeType.TEXT_HTML,
                Map.of()
        );
    }

    protected String getUrl(String request) {
        return request.split(System.lineSeparator())[0].split(" ")[1].split("\\?")[0];
    }
}
