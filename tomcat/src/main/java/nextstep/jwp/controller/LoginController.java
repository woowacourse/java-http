package nextstep.jwp.controller;

import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.QueryParameters;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final SessionManager sessionManager = new SessionManager();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        QueryParameters queryParameters = new QueryParameters(request.getRequestBody());

        try {
            User user = findUser(queryParameters.find("account"));
            validatePassword(user, queryParameters.find("password"));

            log.info("user : " + user);
            addCookieToHeader(request, response, user);
            response.createRedirectResponse("/index.html");
        } catch (UnAuthorizedException e) {
            response.createRedirectResponse("/401.html");
        }
    }

    private User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UnAuthorizedException::new);
    }

    private void validatePassword(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new UnAuthorizedException();
        }
    }

    private void addCookieToHeader(HttpRequest request, HttpResponse response, User user) {
        if (request.getRequestHeaders().isExistCookie() && isNotNullSession(request)) {
            return;
        }
        UUID uuid = UUID.randomUUID();
        storeSession(uuid, user);
        response.addHeader("Set-Cookie: JSESSIONID=" + uuid);
    }

    private void storeSession(UUID uuid, User user) {
        Session session = new Session(uuid.toString());
        session.setAttribute("user", user);
        sessionManager.add(session);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (isNotNullSession(request)) {
            response.createRedirectResponse("/index.html");
            return;
        }
        response.createStaticFileResponse(request.getPath().concat("." + ContentType.HTML.getExtension()));
    }

    private boolean isNotNullSession(HttpRequest request) {
        if (request.getRequestHeaders().isExistCookie()) {
            HttpCookie cookie = request.getRequestHeaders().getCookie();
            String jsessionid = cookie.find("JSESSIONID");
            Session session = sessionManager.findSession(jsessionid);
            return session != null;
        }
        return false;
    }
}
