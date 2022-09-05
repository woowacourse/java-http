package nextstep.jwp.controller;

import nextstep.jwp.support.Resource;
import org.apache.http.Headers;
import org.apache.http.HttpHeader;
import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;

public class ResourceController implements Controller {

    @Override
    public ResponseEntity execute(final RequestEntity request) {
        final Resource resource = new Resource(request.getUri());
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue());
        return new ResponseEntity(headers).content(resource.read());
    }
}
