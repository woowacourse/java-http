package nextstep.jwp.controller;

import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.StatusLine;

public class StaticResourceController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest request) {
        final HttpMethod method = request.getHttpMethod();
        final String uri = request.getRequestUri();
        return method.equals(HttpMethod.GET) && ContentType.isSupportedType(uri);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.OK));
        response.addHeader("Content-Type", ContentType.getTypeByExtension(request.getRequestUri()));
        final String content = readResponseBody(request.getRequestUri());
        response.setResponseBody(content);
    }
}
