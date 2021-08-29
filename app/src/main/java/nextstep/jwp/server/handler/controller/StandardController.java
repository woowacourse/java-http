package nextstep.jwp.server.handler.controller;

import nextstep.jwp.http.message.element.cookie.ProxyHttpCookie;
import nextstep.jwp.http.message.element.session.ProxyHttpSession;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.response.Response;

public class StandardController implements Controller{

    private final Controller controller;

    public StandardController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public Response doService(HttpRequest httpRequest) {
        ProxyHttpCookie proxyHttpCookie = (ProxyHttpCookie) httpRequest.getCookie();
        Response response = controller.doService(httpRequest);

        ProxyHttpSession session = (ProxyHttpSession) httpRequest.getSession();

        if(session.isNew()) {
            proxyHttpCookie.put("JSESSIONID", session.getSessionId());
        }

        if(proxyHttpCookie.isChanged()) {
            response.setCookies(proxyHttpCookie);
        }

        return response;
    }

    @Override
    public boolean isSatisfiedBy(HttpRequest httpRequest) {
        return controller.isSatisfiedBy(httpRequest);
    }
}
