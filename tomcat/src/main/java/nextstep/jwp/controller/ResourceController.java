package nextstep.jwp.controller;

import nextstep.jwp.support.Resource;
import nextstep.jwp.http.Headers;
import org.apache.http.HttpHeader;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;

public class ResourceController implements Controller {

    @Override
    public Response execute(final Request request) {
        final Resource resource = new Resource(request.getUri());
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue());
        return new Response(headers).content(resource.read());
    }
}
