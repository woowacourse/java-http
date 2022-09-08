package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.element.Path;
import servlet.mapping.ResponseEntity;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, ResponseEntity entity) {
        Path path = request.getPath();
        entity.clone(ResponseEntity.ok(path.getPath()));
    }

    @Override
    public boolean isMapped(HttpRequest request) {
        return request.getPath().contains(".");
    }
}
