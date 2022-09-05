package nextstep.jwp.controller;

import nextstep.jwp.support.Resource;
import nextstep.jwp.http.Headers;
import org.apache.http.HttpHeader;
import nextstep.jwp.http.RequestEntity;
import nextstep.jwp.http.ResponseEntity;

public class ResourceController implements Controller {

    @Override
    public ResponseEntity execute(final RequestEntity request) {
        final Resource resource = new Resource(request.getUri());
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue());
        return new ResponseEntity(headers).content(resource.read());
    }
}
