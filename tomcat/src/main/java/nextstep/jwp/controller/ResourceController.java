package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Resource;

public class ResourceController extends AbstractController {

    @Override
    protected final HttpResponse doGet(HttpRequest request) {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body(new Resource(request.getPath()));
    }
}
