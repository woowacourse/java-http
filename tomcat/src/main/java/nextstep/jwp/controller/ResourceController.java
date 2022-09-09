package nextstep.jwp.controller;

import org.apache.http.Request;
import org.apache.http.Response;
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
