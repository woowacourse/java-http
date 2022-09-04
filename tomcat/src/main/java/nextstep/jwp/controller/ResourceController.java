package nextstep.jwp.controller;

import javassist.NotFoundException;
import nextstep.jwp.support.Resource;
import org.apache.http.HttpStatus;
import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;

public class ResourceController implements Controller {

    @Override
    public ResponseEntity execute(final RequestEntity request) throws NotFoundException, URISyntaxException, IOException {
        final Resource resource = new Resource(request.getUri());
        return new ResponseEntity(HttpStatus.OK, resource.getContentType(), resource.read());
    }
}
