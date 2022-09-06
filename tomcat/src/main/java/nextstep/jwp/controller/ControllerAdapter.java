package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ControllerAdapter {

    public static HttpResponse doService(Controller controller, HttpRequest request) {
        if (request.isGet()) {
            return controller.doGet(request);
        } else if (request.isPost()) {
            return controller.doPost(request);
        }
        return HttpResponse.notFound();
    }

}
