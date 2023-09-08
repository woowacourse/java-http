package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.ViewResolver.resolveView;
import static org.apache.coyote.http11.controller.URIPath.DEFAULT_URI;
import static org.apache.coyote.http11.types.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.types.HttpStatus.OK;

public class DefaultController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (DEFAULT_URI.equals(request.getPath())) {
            response.setBody("Hello world!", TEXT_HTML);
            response.setHttpStatus(OK);
            return;
        }

        resolveView(request, response);
    }
}
