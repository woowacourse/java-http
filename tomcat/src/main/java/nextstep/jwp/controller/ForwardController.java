package nextstep.jwp.controller;

import nextstep.jwp.http.Headers;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.support.Resource;
import nextstep.jwp.support.ResourceSuffix;
import org.apache.http.HttpHeader;

public class ForwardController implements Controller {

    @Override
    public Response execute(final Request request) {
        final String uri = request.getUri();
        final Resource resource = new Resource(uri + ResourceSuffix.HTML.getValue());

        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue());
        return new Response(headers).content(resource.read());
    }
}
