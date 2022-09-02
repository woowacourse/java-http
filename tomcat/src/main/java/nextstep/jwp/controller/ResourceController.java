package nextstep.jwp.controller;

import javassist.NotFoundException;
import nextstep.jwp.ResourceReader;
import org.apache.http.HttpStatus;
import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;

import java.net.URISyntaxException;

public class ResourceController implements Controller {

    @Override
    public ResponseEntity execute(final RequestEntity request) throws NotFoundException, URISyntaxException {
        final String content = new ResourceReader().read(request.getUri());
        return new ResponseEntity(HttpStatus.OK, request.getContentType(), content);
    }
}
