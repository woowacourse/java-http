package nextstep.jwp.presentation;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final RegisterController instance = new RegisterController();

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        return instance;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Controller staticResourceController = StaticResourceController.getInstance();
        staticResourceController.service(request, response);
    }
}
