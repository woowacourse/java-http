package nextstep.jwp.controller;

import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.Session;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.StatusLine;
import org.apache.coyote.common.SessionManager;

public class LoginPageController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest request) {
        final HttpMethod method = request.getHttpMethod();
        final String uri = request.getRequestUri();
        return method.equals(HttpMethod.GET) && uri.equals("login");
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final String sessionId = request.getCookies().ofSessionId("JSESSIONID");
        final Session session = SessionManager.findSession(sessionId);
        if (session != null) {
            response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.FOUND));
            response.addHeader("Location", "index.html");
            return;
        }
        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.OK));
        response.addHeader("Content-Type", ContentType.HTML.getType());
        final String content = readResponseBody(request.getRequestUri() + ".html");
        response.setResponseBody(content);
    }
}
