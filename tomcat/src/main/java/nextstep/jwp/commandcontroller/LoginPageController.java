package nextstep.jwp.commandcontroller;

import nextstep.jwp.common.ContentType;
import nextstep.jwp.common.HttpMethod;
import nextstep.jwp.common.HttpStatus;
import nextstep.jwp.model.Session;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.response.StatusLine;
import org.apache.catalina.SessionManager;

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
