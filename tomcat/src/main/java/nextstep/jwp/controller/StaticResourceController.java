package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController {

    public HttpResponse handle(final HttpRequest request) {
        return HttpResponse.ok().fileBody(request.getUriPath()).build();
    }
}
