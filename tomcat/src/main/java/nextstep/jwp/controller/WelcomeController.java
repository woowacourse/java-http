package nextstep.jwp.controller;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import servlet.mapping.ResponseEntity;

public class WelcomeController extends AbstractController {

    @Override
    protected ResponseEntity doGet(HttpRequest request) {
        return ResponseEntity.ok("/welcome.html");
    }

    @Override
    public boolean isMapped(HttpRequest request) {
        return request.getPath().isIn(List.of("/", ""));
    }
}
