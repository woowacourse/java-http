package org.apache.catalina.controller;

import nextstep.mvc.ResponseWriter;
import org.apache.catalina.controller.config.RequestMapping;
import org.apache.catalina.controller.exception.MethodNotAllowedException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

@RequestMapping("/index")
public class IndexController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        ResponseWriter.view(httpResponse, HttpStatus.OK, httpRequest.getUri());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        throw new MethodNotAllowedException();
    }
}
