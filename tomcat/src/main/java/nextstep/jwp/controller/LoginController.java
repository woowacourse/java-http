package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.common.HttpStatus.*;

public class LoginController extends AbstractController {

    private static final String USER_ATTRIBUTE = "user";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        Session session = request.getSession();
        if (session != null) {
            Session savedSession = SessionManager.findSession(session.getId());
            Object sessionUserAttribute = session.getAttribute(USER_ATTRIBUTE);
            if (savedSession != null && sessionUserAttribute !=null && sessionUserAttribute.equals(savedSession.getAttribute(USER_ATTRIBUTE))) {
                response.httpStatus(FOUND)
                        .header("Location", "/index.html")
                        .redirectUri("/index.html");
                return;
            }
        }
        response.httpStatus(OK)
                .redirectUri("/login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        RequestBody requestBody = request.getRequestBody();
        String account = requestBody.getContentValue("account");
        String password = requestBody.getContentValue("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        if (user.checkPassword(password)) {
            request.getSession().setAttribute(USER_ATTRIBUTE, user);
            response.httpStatus(FOUND)
                    .header("Location", "/index.html")
                    .redirectUri("/index.html");
            return;
        }
        response.httpStatus(UNAUTHORIZED)
                .header("Location", "/401.html")
                .redirectUri("/401.html");
    }
}
