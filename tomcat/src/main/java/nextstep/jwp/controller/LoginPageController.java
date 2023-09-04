package nextstep.jwp.controller;

import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_TYPE;

import java.io.IOException;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.handler.Controller;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class LoginPageController implements Controller {

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return "/login".equals(httpRequest.getPath())
                && HttpMethod.GET == httpRequest.getHttpMethod();
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final String sessionId = extractSessionId(httpRequest);
        if (sessionId == null) {
            return redirectPage("login");
        }
        final Session session = SessionManager.findSession(sessionId);
        if (session == null) {
            return redirectPage("login");
        }
        return redirectPage("index");
    }

    private String extractSessionId(final HttpRequest httpRequest) {
        final String cookieHeader = httpRequest.getHeaders().get(Cookie.getNAME());
        final Cookie cookie = Cookie.from(cookieHeader);
        return cookie.getAttribute(Session.getName());
    }

    private HttpResponse redirectPage(final String viewName) throws IOException {
        final String body = ViewResolver.findView(viewName);
        final HttpResponse httpResponse = new HttpResponse(StatusCode.OK, body);
        httpResponse.addHeader(CONTENT_TYPE, ContentType.HTML.getContentType());
        httpResponse.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return httpResponse;
    }
}
