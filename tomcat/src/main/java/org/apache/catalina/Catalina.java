package org.apache.catalina;

import org.apache.catalina.connector.HandlerDispatcher;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class Catalina {

    private final HandlerDispatcher handlerDispatcher;

    public Catalina(final HandlerDispatcher handlerDispatcher) {
        this.handlerDispatcher = handlerDispatcher;

    }

    public void service(final Http11Request request, final Http11Response response) {
        final SessionManager sessionManager = SessionManager.getInstance();
        // 요청 처리
        handlerDispatcher.handle(request, response);
        // 세션 추가
    }
}
