package nextstep.jwp.controller;

import static org.apache.coyote.HttpStatus.FOUND;
import static org.apache.coyote.HttpStatus.OK;
import static org.apache.coyote.header.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.header.HttpHeaders.CONTENT_TYPE;
import static org.apache.coyote.header.HttpHeaders.LOCATION;
import static org.apache.coyote.header.HttpMethod.GET;

import nextstep.jwp.util.ResourceLoaderUtil;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.header.ContentType;
import org.apache.coyote.header.HttpCookie;
import org.apache.coyote.http11.handler.Controller;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPageController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginPageController.class);

    @Override
    public boolean support(HttpRequest request) {
        return request.httpMethod().equals(GET) && request.requestUrl().startsWith("/login");
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        response.setVersion(request.protocolVersion());

        String cookieValue = request.getHeaderValue("Cookie");
        HttpCookie cookie = new HttpCookie(cookieValue);
        String sessionId = cookie.getValue("JSESSIONID");
        Session session = SessionManager.findSession(sessionId);
        if (session != null) {
            response.setStatus(FOUND);
            response.addHeader(LOCATION, "index.html");
            return;
        }

        String content = ResourceLoaderUtil.loadContent(request.requestUrl());
        response.setStatus(OK);
        response.addHeader(CONTENT_TYPE, ContentType.negotiate(request.requestUrl()));
        response.addHeader(CONTENT_LENGTH, String.valueOf(content.getBytes().length));
        response.setBody(content);
    }
}
