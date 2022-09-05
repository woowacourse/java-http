package nextstep.jwp.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class ResourceController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) throws Exception {
        return HttpResponse.ofResource(httpRequest.getPath());
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return httpRequest.isForResource();
    }
}
