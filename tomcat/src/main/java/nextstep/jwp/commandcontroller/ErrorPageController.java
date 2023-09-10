package nextstep.jwp.commandcontroller;

import nextstep.jwp.common.ContentType;
import nextstep.jwp.common.HttpStatus;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.response.StatusLine;

public class ErrorPageController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest request) {
        return super.canHandle(request);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.NOT_FOUND));
        response.addHeader("Content-Type", ContentType.HTML.getType());
        final String content = readResponseBody("500.html");
        response.setResponseBody(content);
    }
}
