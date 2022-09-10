package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.element.Path;
import servlet.mapping.ResponseEntity;

public class ResourceController extends AbstractController {

    @Override
    protected ResponseEntity doGet(HttpRequest request) {
        Path path = request.getPath();
        return ResponseEntity.ok(path.getPath());
    }

    @Override
    public boolean isMapped(HttpRequest request) {
        return request.getPath().contains(".");
    }
}
