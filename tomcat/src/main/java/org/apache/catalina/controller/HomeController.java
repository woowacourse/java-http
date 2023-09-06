package org.apache.catalina.controller;

import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.response.StatusCode.OK;

import java.util.List;

import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Response;

public class HomeController extends HttpController {

    public static final String HOME = "/";

    @Override
    public Response service(final Request request) {
        final HttpMethod httpMethod = request.getHttpMethod();

        if (httpMethod.equals(GET)) {
            return doGet(request);
        }

        throw new MethodNotAllowedException(List.of(GET));
    }

    @Override
    protected Response doGet(final Request request) {
        final String responseBody = "Hello world!";
        final HttpVersion httpVersion = request.getHttpVersion();
        final ContentType contentType = ContentType.HTML;
        return Response.of(httpVersion, OK, contentType, responseBody);
    }
}
