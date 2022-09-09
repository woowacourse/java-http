package nextstep.jwp.controller;

import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.support.Resource;
import org.apache.http.HttpHeader;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(final Request request, final Response response) {
        final Resource resource = new Resource(request.getUri());
        response.header(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue())
                .content(resource.read());
    }
}
