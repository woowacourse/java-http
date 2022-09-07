package org.apache.coyote.http11.handler.FileHandler;

import java.util.regex.Pattern;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;
import org.apache.coyote.http11.session.Session;

public class LoginPageHandler implements Handler {
    private static final String USER = "user";

    private static final Pattern LOGIN_PAGE_URI_PATTERN = Pattern.compile("/login.html");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchRequestLine(HttpMethod.GET, LOGIN_PAGE_URI_PATTERN);
    }

    @Override
    public Object handle(HttpRequest httpRequest) {
        Session session = httpRequest.getSession();
        if (session.getAttribute(USER) != null) {
            return new FileHandlerResponse(HttpStatus.FOUND, "/index.html");
        }

        return new FileHandlerResponse(HttpStatus.OK, httpRequest.getPath());
    }
}
