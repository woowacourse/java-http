package org.apache.coyote.http11.pageController;

import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class UserSessionController extends AbstractController {
    private static final String USER_ATTRIBUTE = "user";

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
}
