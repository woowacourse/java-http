package org.apache.catalina.controller;

import static java.util.Objects.requireNonNull;
import static org.apache.catalina.controller.StaticResourceUri.NOT_FOUND_PAGE;
import static org.apache.coyote.http11.response.ResponseContentType.TEXT_HTML;

import java.io.IOException;
import org.apache.catalina.util.FileLoader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestUri;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseContentType;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpRequestUri requestUri = request.getUri();
        final String resource = FileLoader.load(RESOURCE_DIRECTORY + requestUri.getPath());

        if (resource == null) {
            getNotFoundPage(response);
            return;
        }

        response.setStatusCode(HttpStatusCode.OK)
                .addContentTypeHeader(ResponseContentType.from(requestUri.getPath()).getType())
                .addContentLengthHeader(requireNonNull(resource).getBytes().length)
                .setResponseBody(new HttpResponseBody(resource));
    }

    private void getNotFoundPage(final HttpResponse response) throws IOException {
        final String resource = FileLoader.load(RESOURCE_DIRECTORY + NOT_FOUND_PAGE.getUri());

        response.setStatusCode(HttpStatusCode.NOT_FOUND)
                .addContentTypeHeader(TEXT_HTML.getType())
                .addContentLengthHeader(requireNonNull(resource).getBytes().length)
                .setResponseBody(new HttpResponseBody(resource));
    }
}
