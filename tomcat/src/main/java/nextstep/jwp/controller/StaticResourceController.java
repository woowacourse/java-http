package nextstep.jwp.controller;

import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.common.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            final var path = httpRequest.getPath();
            httpResponse.ok(StaticResource.path(path));
        } catch (NotFoundException e) {
            httpResponse.found("/404.html");
        }
    }
}
