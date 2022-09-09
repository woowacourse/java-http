package nextstep.jwp.controller;

import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.support.Resource;
import nextstep.jwp.support.ResourceSuffix;
import org.apache.http.HttpHeader;

public class ForwardController implements Controller {

    @Override
    public void service(final Request request, final Response response) {
        final String uri = request.getUri();
        final Resource resource = new Resource(uri + ResourceSuffix.HTML.getValue());

        response.header(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue())
                .content(resource.read());
    }
}
