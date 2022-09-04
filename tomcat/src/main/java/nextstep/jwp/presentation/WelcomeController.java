package nextstep.jwp.presentation;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpStatus;

public class WelcomeController extends AbstractController {

    private static final WelcomeController instance = new WelcomeController();

    private static final String WELCOME_MESSAGE = "Hello world!";

    private WelcomeController() {
    }

    public static WelcomeController getInstance() {
        return instance;
    }

    @Override
    void doGet(final HttpRequest request, final HttpResponse response) {
        response.setBody(WELCOME_MESSAGE);
        response.setStatus(HttpStatus.OK);
    }
}
