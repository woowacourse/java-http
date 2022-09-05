package nextstep.jwp.controller;

import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final var path = httpRequest.getPath();

        try {
            httpResponse.ok(StaticResource.path(path));
        } catch (NotFoundException e) {
            httpResponse.sendError(HttpStatus.NOT_FOUND, StaticResource.path("/404.html"));
        }
    }
}
