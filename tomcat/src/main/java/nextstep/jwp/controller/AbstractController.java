package nextstep.jwp.controller;

import nextstep.jwp.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.element.HttpMethod;
import servlet.handler.Controller;
import servlet.mapping.ResponseEntity;

public class AbstractController implements Controller {

    private static final String ERROR_405_MESSAGE = "매핑되는 메소드가 없습니다.";

    @Override
    public ResponseEntity service(HttpRequest request) {
        if (request.getMethod() == HttpMethod.GET) {
            return doGet(request);
        }
        if (request.getMethod() == HttpMethod.POST) {
            return doPost(request);
        }
        throw new MethodNotAllowedException(ERROR_405_MESSAGE);
    }

    @Override
    public boolean isMapped(HttpRequest request) {
        return false;
    }


    protected ResponseEntity doPost(HttpRequest request) {
        throw new MethodNotAllowedException(ERROR_405_MESSAGE);
    }

    protected ResponseEntity doGet(HttpRequest request) {
        throw new MethodNotAllowedException(ERROR_405_MESSAGE);
    }
}
