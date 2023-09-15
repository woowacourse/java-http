package nextstep.jwp.controller;

import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpHeader;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.StatusLine;

public class StaticResourceController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest request) {
        return ContentType.isSupportedType(request.getRequestUri());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.OK));
        response.addHeader(HttpHeader.CONTENT_TYPE.getName(), ContentType.getTypeByExtension(request.getRequestUri()));
        final String content = readResponseBody(request.getRequestUri());
        response.setResponseBody(content);
    }
}
