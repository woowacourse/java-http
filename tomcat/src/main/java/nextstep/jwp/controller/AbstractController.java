package nextstep.jwp.controller;

import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;

public abstract class AbstractController implements Controller {

    @Override
    public ResponseEntity handle(HttpRequest request) {
        if (request.getMethod() == HttpMethod.GET) {
            return doGet();
        }
        return doPost(request);
    }

    abstract ResponseEntity doGet();

    abstract ResponseEntity doPost(HttpRequest request);
}
