package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import servlet.handler.AbstractController;
import servlet.mapping.ResponseEntity;

public class WelcomeController extends AbstractController {

    private static final String PATH = "";

    public WelcomeController() {
        super(PATH);
    }

    @Override
    protected ResponseEntity doGet(HttpRequest request) {
        return ResponseEntity.ok("/welcome.html");
    }
}
