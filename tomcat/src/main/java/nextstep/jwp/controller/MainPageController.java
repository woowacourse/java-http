package nextstep.jwp.controller;

import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.StatusLine;

public class MainPageController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getRequestUri().isEmpty();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.OK));
        response.addHeader("Content-Type", ContentType.HTML.getType());
        response.setResponseBody("Hello world!");
    }
}
