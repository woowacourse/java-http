package org.apache.catalina.controller;

import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.response.StatusCode.OK;

import java.io.IOException;
import java.util.List;

import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Response;

public class IndexController extends HttpController {

    public static final String INDEX = "/index";
    public static final String INDEX_HTML = "/index.html";

    @Override
    public Response service(final Request request) throws IOException, MethodNotAllowedException {
        if (request.getHttpMethod().equals(GET)) {
            return doGet(request);
        }
        throw new MethodNotAllowedException(List.of(GET));
    }

    @Override
    protected Response doGet(final Request request) throws IOException {
        final String responseBody = FileReader.read(INDEX_HTML);
        final ContentType contentType = ContentType.findByName(INDEX_HTML);
        return Response.of(request.getHttpVersion(), OK, contentType, responseBody);
    }
}
