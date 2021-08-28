package nextstep.jwp;

import nextstep.jwp.network.HttpRequest;
import nextstep.jwp.network.HttpResponse;
import nextstep.jwp.network.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    public RegisterController(String resource) {
        super(resource);
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        return new HttpResponse(HttpStatus.OK, readFile(getResource()));
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest) {
        return new HttpResponse(HttpStatus.OK, readIndex());
    }
}
