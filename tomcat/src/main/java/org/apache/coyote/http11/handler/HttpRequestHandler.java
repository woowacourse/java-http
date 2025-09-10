package org.apache.coyote.http11.handler;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

import java.nio.charset.StandardCharsets;

public abstract class HttpRequestHandler {

    public abstract String getSupportedUrl();

    public final HttpResponse handle(HttpRequest request) {
        validateSupports(request);
        HttpMethod requestMethod = request.method();
        if (requestMethod.equals(HttpMethod.GET)) {
            return handleGet(request);
        } else if (requestMethod.equals(HttpMethod.POST)) {
            return handlePost(request);
        }
        return handleMethodNotAllow(request);
    }

    protected abstract HttpResponse handleGet(HttpRequest request);

    protected abstract HttpResponse handlePost(HttpRequest request);

    private void validateSupports(HttpRequest request) {
        if (!supportsUrl(request.url())) {
            throw new IllegalArgumentException("URL " + request.url() + " not supported.");
        }
    }

    private boolean supportsUrl(String url) {
        return getSupportedUrl().equals(url);
    }

    private HttpResponse handleMethodNotAllow(HttpRequest request) {
        HttpMethod requestMethod = request.method();
        ResponseBody responseBody = new ResponseBody(
                String.format("Http Method %s not allowed", requestMethod).getBytes(StandardCharsets.UTF_8),
                MimeType.TEXT_HTML
        );
        return HttpResponse.http11Builder(HttpStatus.METHOD_NOT_ALLOWED)
                .body(responseBody)
                .build();
    }

    protected String getUrl(String request) {
        return request.split("\r\n")[0].split(" ")[1].split("\\?")[0];
    }
}
