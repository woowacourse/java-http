package nextstep.jwp.controller;

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
    }

    @Override
    public boolean isMapped(HttpRequest request) {
        return false;
    }


    protected void doPost(HttpRequest request, ResponseEntity entity) { /* NOOP */ }

    protected void doGet(HttpRequest request, ResponseEntity entity) { /* NOOP */ }
}
