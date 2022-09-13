package nextstep.jwp.controller;

import org.apache.coyote.http11.exception.ResourceNotFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Resource;

public class ResourceController extends AbstractController {

    @Override
    protected final HttpResponse doGet(final HttpRequest request) {
        try {
            return HttpResponse.ok()
                    .body(new Resource(request.getPath()));
        } catch (final ResourceNotFoundException e) {
            return fail(HttpStatus.NOT_FOUND, Page.NOT_FOUND);
        }
    }
}
