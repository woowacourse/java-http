package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.view.View;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Cookie;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class LoginController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String USER_KEY = "user";

    @Override
    public HttpResponse service(HttpRequest request) {
        RequestLine requestLine = request.getRequestLine();
        Method method = requestLine.getMethod();

        if (method.isPost()) {
            return doPost(request);
        }
        if (method.isGet()) {
            return doGet(request);
        }

        return doNotFoundRequest();
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();
        String account = requestBody.getValue(ACCOUNT_KEY);
        String password = requestBody.getValue(PASSWORD_KEY);

        return InMemoryUserRepository.findByAccount(account)
                .map(user -> getLoginResponse(user, password))
                .orElse(getUnauthorizedResponse());
    }

    private HttpResponse getUnauthorizedResponse() {
        return HttpResponse.redirect()
                .addLocation(View.UNAUTHORIZED.getViewFileName())
                .build();
    }

    private HttpResponse getLoginResponse(User user, String password) {
        if (user.checkPassword(password)) {
            Session session = new Session();
            session.setAttribute(USER_KEY, user);
            SessionManager.add(session);

            return getLoginRedirectResponse(session);
        }
        return getUnauthorizedResponse();
    }

    private HttpResponse getLoginRedirectResponse(Session session) {
        return HttpResponse.redirect()
                .addLocation(View.INDEX.getViewFileName())
                .addCookie(Cookie.ofJSessionId(session.getId()))
                .build();
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        if (isAlreadyLogin(request)) {
            return HttpResponse.redirect()
                    .addLocation(View.INDEX.getViewFileName())
                    .build();
        }
        return HttpResponse.ok()
                .addResponseBody(View.LOGIN.getContents(), ContentType.TEXT_HTML_CHARSET_UTF_8)
                .build();
    }

    private boolean isAlreadyLogin(HttpRequest request) {
        Optional<String> session = request.getSession();
        if (session.isEmpty()) {
            return false;
        }
        return SessionManager.isExist(session.get());
    }
}
