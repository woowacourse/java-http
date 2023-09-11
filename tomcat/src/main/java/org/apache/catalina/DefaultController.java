package org.apache.catalina;

import java.io.IOException;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultController extends AbstractController {

    private static final String INTERNAL_SERVER_ERROR = "/500";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        setResponse(response, INTERNAL_SERVER_ERROR, HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final String resource = request.getRequestPath().getResource();

        setResponse(response, resource, HttpStatusCode.OK);
    }
}
