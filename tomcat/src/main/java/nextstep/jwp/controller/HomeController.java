package nextstep.jwp.controller;

import org.apache.coyote.http11.common.header.ContentTypeValue;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.statusLine.HttpStatus;

public class HomeController extends AbstractController {

    private static final String SUPPORT_URI_PATH = "/";

    @Override
    public boolean support(final HttpRequest request) {
        return request.isPathOf(SUPPORT_URI_PATH);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final ResponseEntity responseEntity = ResponseEntity.of(HttpStatus.NOT_FOUND);

        response.setResponse(HttpResponse.of(request.getHttpVersion(), responseEntity));
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK, ContentTypeValue.TEXT_HTML, "Hello world!");

        response.setResponse(HttpResponse.of(request.getHttpVersion(), responseEntity));
    }
}
