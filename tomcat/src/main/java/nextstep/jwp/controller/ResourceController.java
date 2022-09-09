package nextstep.jwp.controller;

import org.apache.coyote.support.Request;
import org.apache.coyote.support.Response;
import nextstep.jwp.support.Resource;
import org.apache.coyote.HttpHeader;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(final Request request, final Response response) {
        final Resource resource = new Resource(request.getUri());
        response.header(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue())
                .content(resource.read());
    }
}
