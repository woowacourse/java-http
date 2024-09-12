package org.apache.catalina.controller;

import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.RequestLine;

public abstract class AbstractController implements Controller {

    protected static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        RequestLine requestLine = request.getRequestLine();
        HttpMethod method = requestLine.getHttpMethod();
        if (method.isGet()) {
            doGet(request, response);
        } else if (method.isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(Http11Request request, Http11Response response) throws Exception {/* NOOP */}

    protected void doPost(Http11Request request, Http11Response response) throws Exception {/* NOOP */}
}
