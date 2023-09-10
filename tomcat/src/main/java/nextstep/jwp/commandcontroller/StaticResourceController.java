package nextstep.jwp.commandcontroller;

import nextstep.jwp.common.ContentType;
import nextstep.jwp.common.HttpMethod;
import nextstep.jwp.common.HttpStatus;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.response.StatusLine;

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
