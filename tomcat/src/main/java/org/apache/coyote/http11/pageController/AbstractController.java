package org.apache.coyote.http11.pageController;

import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import java.io.IOException;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements PageController {
    private static final String USER_ATTRIBUTE = "user";

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        switch (httpRequest.getMethod()) {
            case GET -> doGet(httpRequest, httpResponse);
            case POST -> doPost(httpRequest, httpResponse);
            default -> methodNotAllowed(httpRequest, httpResponse);
        }
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        methodNotAllowed(httpRequest, httpResponse);
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        methodNotAllowed(httpRequest, httpResponse);
    }

    protected boolean isLogIn(HttpRequest httpRequest) {
        Session session = httpRequest.getSession(false);

        return session != null && getUser(session) != null;
    }

    protected void loginAndRedirect(HttpRequest httpRequest, HttpResponse httpResponse, User user, String location) {
        Session session = httpRequest.renewSession();
        session.setAttribute(USER_ATTRIBUTE, user);

        httpResponse.sendRedirect(location);
        httpResponse.addCookie(HttpCookie.JSESSIONID, session.getId());
    }

    private User getUser(Session session) {
        return (User) session.getAttribute(USER_ATTRIBUTE);
    }

    private void methodNotAllowed(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        httpResponse.setBody("text/plain", "지원하지 않는 HTTP 메서드입니다: " + httpRequest.getMethod());
    }
}
