package nextstep.jwp.controller;

import static common.ResponseStatus.OK;
import static org.apache.coyote.request.line.HttpMethod.GET;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class ResourceController extends AbstractController {

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        return super.canProcess(httpRequest);
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.consistsOf(GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setResponseResource(OK, httpRequest.requestUri());
    }
}
