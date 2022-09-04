package nextstep.jwp.controller;

import nextstep.jwp.support.Resource;
import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;

public class ResourceController implements Controller {

    @Override
    public ResponseEntity execute(final RequestEntity request) {
        final Resource resource = new Resource(request.getUri());
        return new ResponseEntity().contentType(resource.getContentType())
                .content(resource.read());
    }
}
