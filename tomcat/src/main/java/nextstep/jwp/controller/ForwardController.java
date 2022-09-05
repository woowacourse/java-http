package nextstep.jwp.controller;

import nextstep.jwp.http.Headers;
import nextstep.jwp.http.RequestEntity;
import nextstep.jwp.http.ResponseEntity;
import nextstep.jwp.support.Resource;
import nextstep.jwp.support.ResourceSuffix;
import org.apache.http.HttpHeader;

public class ForwardController implements Controller {

    @Override
    public ResponseEntity execute(final RequestEntity requestEntity) {
        final String uri = requestEntity.getUri();
        final Resource resource = new Resource(uri + ResourceSuffix.HTML.getValue());

        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue());
        return new ResponseEntity(headers).content(resource.read());
    }
}
