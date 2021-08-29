package nextstep.jwp.server.handler.controller;

import nextstep.jwp.http.message.element.cookie.ProxyCookie;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.response.Response;

public class StandardController implements Controller{

    private final Controller controller;

    public StandardController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public Response doService(HttpRequest httpRequest) {
        ProxyCookie proxyCookie = (ProxyCookie) httpRequest.getCookie();
        Response response = controller.doService(httpRequest);

        if(proxyCookie.isChanged()) {
            response.setCookies(proxyCookie);
        }

        return response;
    }

    @Override
    public boolean isSatisfiedBy(HttpRequest httpRequest) {
        return controller.isSatisfiedBy(httpRequest);
    }
}
