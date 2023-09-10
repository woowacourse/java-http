package org.apache.catalina.controller;

import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_TYPE;

import java.util.Objects;
import org.apache.catalina.FileLoader;
import org.apache.catalina.ResponseContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpStatusCode;

public class DefaultController extends AbstractController {

    public static final String DEFAULT_PAGE = "/index.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String resource = FileLoader.load(RESOURCE_DIRECTORY + DEFAULT_PAGE);

        response.setHttpVersion(request.getHttpVersion())
                .setStatusCode(HttpStatusCode.OK)
                .addHeader(CONTENT_TYPE, ResponseContentType.TEXT_HTML.getType())
                .addHeader(CONTENT_LENGTH, Objects.requireNonNull(resource).getBytes().length)
                .setResponseBody(new HttpResponseBody(resource));
    }
}
