package nextstep.jwp.controller;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import servlet.mapping.ResponseEntity;

public class WelcomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, ResponseEntity entity) {
        entity.clone(ResponseEntity.ok("/welcome.html"));
    }

    @Override
    public boolean isMapped(HttpRequest request) {
        return request.getPath().isIn(List.of("/", ""));
    }
}
