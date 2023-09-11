package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.controller.support.FileFinder;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.ContentType;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StatusCode;

public class PageController extends AbstractController {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String CONTENT_LENGTH = "Content-Length";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String pathInfo = request.getPath();
        final String body = FileFinder.find(pathInfo);

        response.setStatusCode(StatusCode.OK);
        response.addHeader(CONTENT_TYPE, ContentType.find(pathInfo).getValue() + CHARSET_UTF_8);
        response.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.setBody(body);
    }
}
