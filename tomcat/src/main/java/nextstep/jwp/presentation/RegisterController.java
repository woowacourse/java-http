package nextstep.jwp.presentation;

import java.io.File;
import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String PREFIX = "static";

    public static RegisterController instance = new RegisterController();

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        return instance;
    }

    @Override
    void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        File file = new File(Thread.currentThread().getContextClassLoader().getResource(PREFIX + "/register.html").getFile());
        httpResponse.addResponseBody(file);
    }

    @Override
    void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {

    }
}
