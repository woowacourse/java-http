package nextstep.jwp.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
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
        final String cookieHeader = httpRequest.getHeaders().get("Cookie");
        final Cookie cookie = Cookie.from(cookieHeader);
        final String sessionId = cookie.getAttribute("JSESSIONID");
        if (sessionId == null) {
            return redirectPage("login");
        }
        final Session session = SessionManager.findSession(sessionId);
        if(session == null) {
            return redirectPage("login");
        }
        return redirectPage("index");
    }

    private HttpResponse redirectPage(final String viewName) throws IOException {
        final String body = ViewResolver.findView(viewName);
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", ContentType.HTML.getContentType());
        headers.put("Content-Length", String.valueOf(body.getBytes().length));

        return new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                headers,
                body
        );
    }
}
