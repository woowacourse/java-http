package nextstep.jwp.controller;

import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController implements Controller {

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        if (httpRequest.getMethod().equals(HttpMethod.GET)) {
            return show(httpRequest.getPath());
        }
        return HttpResponse.found("/404.html");
    }

    public HttpResponse show(final String path) {
        try {
            return HttpResponse.ok(StaticResource.path(path));
        } catch (NotFoundException e) {
            return HttpResponse.found("/404.html");
        }
    }
}
