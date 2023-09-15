package org.apache.catalina;

import org.apache.coyote.http11.Servlet;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.response.HttpResponse;

public class DispatcherServlet implements Servlet {

    private final RequestMapping requestMapping;

    public DispatcherServlet(final RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        Handler handler = requestMapping.getHandler(httpRequest);
        setSessionOnHttpRequest(httpRequest);
        handler.handle(httpRequest, httpResponse);
        addSessionToHttpResponse(httpRequest, httpResponse);
    }

    private void setSessionOnHttpRequest(final HttpRequest httpRequest) {
        RequestHeader requestHeader = httpRequest.getRequestHeader();
        HttpCookie cookie = requestHeader.getCookie();
        String jsessionid = cookie.getCookieValue("JSESSIONID");
        Session session = SessionManager.findSession(jsessionid);
        if (session == null) {
            return;
        }
        session.setFirstSent(false);
        httpRequest.setSession(session);
    }

    private void addSessionToHttpResponse(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        Session session = httpRequest.getSession();
        if (session == null) {
            Session newSession = new Session();
            SessionManager.addSession(newSession);
            httpResponse.setSession(newSession);
            return;
        }
        httpResponse.setSession(session);
    }
}
