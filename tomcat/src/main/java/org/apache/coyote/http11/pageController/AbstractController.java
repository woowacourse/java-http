package org.apache.coyote.http11.pageController;

import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements PageController {
    private static final String USER_ATTRIBUTE = "user";

    @Override
    public HttpResponse run(HttpRequest httpRequest) throws IOException {
        return switch (httpRequest.getMethod()) {
            case GET -> doGet(httpRequest);
            case POST -> doPost(httpRequest);
            default -> methodNotAllowed(httpRequest);
        };
    }

    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        return methodNotAllowed(httpRequest);
    }

    protected HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        return methodNotAllowed(httpRequest);
    }

    protected HttpResponse redirect(String location) {
        return new HttpResponse(HttpStatus.FOUND, Map.of("Location", location), "");
    }

    protected boolean isLogIn(HttpRequest httpRequest) {
        Session session = httpRequest.getSession(false);

        return session != null && getUser(session) != null;
    }

    protected HttpResponse loginAndRedirect(HttpRequest httpRequest, User user, String location) {
        Session session = httpRequest.renewSession();
        session.setAttribute(USER_ATTRIBUTE, user);

        HttpResponse response = redirect(location);
        response.addCookie(HttpCookie.JSESSIONID, session.getId());

        return response;
    }

    private User getUser(Session session) {
        return (User) session.getAttribute(USER_ATTRIBUTE);
    }

    private HttpResponse methodNotAllowed(HttpRequest httpRequest) {
        return HttpResponse.of(
                HttpStatus.METHOD_NOT_ALLOWED,
                "text/plain",
                "지원하지 않는 HTTP 메서드입니다: " + httpRequest.getMethod()
        );
    }
}
