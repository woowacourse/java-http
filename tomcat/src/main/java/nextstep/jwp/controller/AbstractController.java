package nextstep.jwp.controller;

import java.util.List;
import nextstep.jwp.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.element.HttpMethod;
import servlet.mapping.ResponseEntity;

public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, ResponseEntity entity) {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, entity);
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, entity);
        }
        if (!List.of(HttpMethod.GET, HttpMethod.POST).contains(request.getMethod())) {
            throw new MethodNotAllowedException("매핑되는 메소드가 없습니다.");
        }
    }

    @Override
    public boolean isMapped(HttpRequest request) {
        return false;
    }


    protected void doPost(HttpRequest request, ResponseEntity entity) { /* NOOP */ }

    protected void doGet(HttpRequest request, ResponseEntity entity) { /* NOOP */ }
}
