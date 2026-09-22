package org.apache.catalina;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerMapping;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatcher implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";

    private final ControllerMapping controllerMapping;
    private final SessionManager sessionManager;

    public Dispatcher(final ControllerMapping controllerMapping, final SessionManager sessionManager) {
        this.controllerMapping = controllerMapping;
        this.sessionManager = sessionManager;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        try {
            return route(request);
        } catch (Exception e) {
            log.error("요청을 처리하지 못했습니다.", e);

            return HttpResponses.serverError();
        }
    }

    private HttpResponse route(final HttpRequest httpRequest) throws Exception {
        final String path = httpRequest.requestLine().path();
        final Controller controller = controllerMapping.find(path).orElse(null);

        if (controller == null) {
            return HttpResponses.render(path);
        }

        final Request request = new Request(httpRequest, sessionManager);
        final HttpResponse response = controller.handle(request);

        return request.createdSession()
                .map(session -> withSessionCookie(response, session))
                .orElse(response);
    }

    private HttpResponse withSessionCookie(final HttpResponse response, final Session session) {
        final Map<String, String> headers = new LinkedHashMap<>(response.headers());
        headers.put(SET_COOKIE, JSESSIONID + "=" + session.getId());

        return new HttpResponse(response.statusLine(), headers, response.responseBody());
    }
}
