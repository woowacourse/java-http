package org.apache.catalina.controller;

import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.response.ResponseHeaderType.LOCATION;

import java.util.Objects;
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
            response.setHttpVersion(request.getHttpVersion())
                    .setStatusCode(HttpStatusCode.FOUND)
                    .addHeader(CONTENT_TYPE, ResponseContentType.TEXT_HTML.getType())
                    .addHeader(LOCATION, StaticResourceUri.NOT_FOUND_PAGE.getUri());
            return;
        }

        response.setStatusCode(HttpStatusCode.OK)
                .addHeader(CONTENT_TYPE, ResponseContentType.from(requestUri.getPath()).getType())
                .addHeader(CONTENT_LENGTH, Objects.requireNonNull(resource).getBytes().length)
                .setResponseBody(new HttpResponseBody(resource));
    }
}
