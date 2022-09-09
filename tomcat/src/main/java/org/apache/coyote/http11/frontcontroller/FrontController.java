package org.apache.coyote.http11.frontcontroller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.requestmapping.ApiHandlerMapper;
import org.apache.coyote.http11.requestmapping.FileHandlerMapper;
import org.apache.coyote.http11.requestmapping.RequestMapper;

public class FrontController {

    private static final List<RequestMapper> HANDLER_MAPPERS = List.of(new FileHandlerMapper(), new ApiHandlerMapper());

    public void doService(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        try {
            Controller controller = getController(httpRequest);
            controller.service(httpRequest, httpResponse);
        } catch (Exception e) {
            System.out.println("예외발생 : controllerAdvice 추가 예정, controllerAdvice.service(httpRequest, httResponse)");
        }
        httpResponse.write();
    }

    private Controller getController(HttpRequest httpRequest) {
        return HANDLER_MAPPERS.stream()
                .map(mapper -> mapper.mapController(httpRequest))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 있는 요청이 아닙니다."));
    }

//    private void setSession(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
//        String cookieValue = httpRequest.getCookieValue();
//        Cookie cookie = Cookie.of(cookieValue);
//        Manager sessionManager = new SessionManager();
//
//        if (!cookie.hasJSessionId()) {
//            UUID uuid = UUID.randomUUID();
//            httpResponse.setCookie(Cookie.fromJSessionId(uuid));
//            Session session = new Session(uuid.toString());
//            sessionManager.add(session);
//            httpRequest.setSession(session);
//            return;
//        }
//        Session session = getSession(cookie, sessionManager);
//        httpRequest.setSession(session);
//    }
//
//    private Session getSession(Cookie cookie, Manager sessionManager) throws IOException {
//        Session session = sessionManager.findSession(cookie.getSessionKey());
//
//        if (session == null) {
//            session = new Session(cookie.getSessionKey());
//            sessionManager.add(session);
//        }
//        return session;
//    }
}
