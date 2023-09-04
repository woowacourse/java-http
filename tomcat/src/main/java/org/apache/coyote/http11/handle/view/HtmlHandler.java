package org.apache.coyote.http11.handle.view;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.start.HttpVersion;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HtmlHandler implements ViewHandler {

    private static final String CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String HTTP_STATUS_OK = "200 OK";

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        final HttpVersion httpVersion = request.getHttpStartLine().getHttpVersion();
        final String responseBody = makeResponseBody(request);
        return HttpResponse.of(httpVersion, HTTP_STATUS_OK, CONTENT_TYPE, responseBody);
    }

    private String makeResponseBody(final HttpRequest requestTarget) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(
                "static/" + requestTarget.getHttpStartLine().getRequestTarget().getResources() + requestTarget.getHttpStartLine().getRequestTarget().getExtensionName());
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
