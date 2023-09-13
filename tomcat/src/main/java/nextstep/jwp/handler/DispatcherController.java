package nextstep.jwp.handler;

import nextstep.jwp.handler.mappaing.HandlerMapping;
import org.apache.catalina.servlet.Controller;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;

public class DispatcherController implements Controller {

    private final HandlerMapping mapping;

    public DispatcherController(HandlerMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        Controller handler = mapping.getHandler(request);
        handler.service(request, response);
    }
}
