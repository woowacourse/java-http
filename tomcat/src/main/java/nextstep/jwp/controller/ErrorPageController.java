package nextstep.jwp.controller;

import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpHeader;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.StatusLine;

public class ErrorPageController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest request) {
        return false;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.NOT_FOUND));
        response.addHeader(HttpHeader.CONTENT_TYPE.getName(), ContentType.HTML.getType());
        final String content = readResponseBody("500.html");
        response.setResponseBody(content);
    }
}
