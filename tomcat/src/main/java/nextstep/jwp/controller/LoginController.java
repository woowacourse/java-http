package nextstep.jwp.controller;

import java.util.Optional;
import java.util.UUID;
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
        String account = requestBody.getValue("account");
        String password = requestBody.getValue("password");

        return InMemoryUserRepository.findByAccount(account)
                .map(user -> getLoginResponse(user, password))
                .orElse(getUnauthorizedResponse());
    }

    private HttpResponse getLoginResponse(User user, String password) {
        if (user.checkPassword(password)) {
            UUID uuid = UUID.randomUUID();
            Session session = new Session(uuid.toString());
            session.setAttribute("user", user);
            SessionManager.add(session);

            return HttpResponse.redirect()
                    .addLocation(View.INDEX.getViewFileName())
                    .addCookie(Cookie.ofJSessionId(session.getId()))
                    .build();
        }
        return getUnauthorizedResponse();
    }

    private HttpResponse getUnauthorizedResponse() {
        return HttpResponse.redirect()
                .addLocation(View.UNAUTHORIZED.getViewFileName())
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
