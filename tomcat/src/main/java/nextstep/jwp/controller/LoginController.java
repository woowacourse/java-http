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
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Cookies;
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
        Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.getValue("account"));
        if (user.isPresent()) {
            if (user.get().checkPassword(requestBody.getValue("password"))) {
                UUID uuid = UUID.randomUUID();
                Session session = new Session(uuid.toString());
                session.setAttribute("user", user.get());
                SessionManager.add(session);

                return HttpResponse.redirect()
                        .addLocation(View.INDEX.getViewFileName())
                        .addCookie(Cookies.ofJSessionId(session.getId()));
            }
        }
        return HttpResponse.redirect()
                .addLocation(View.UNAUTHORIZED.getViewFileName());
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        Optional<String> session = request.getSession();
        if (session.isPresent() && SessionManager.findSession(session.get()).isPresent()) {
            return HttpResponse.redirect()
                    .addLocation(View.INDEX.getViewFileName());
        }

        return HttpResponse.ok()
                .addResponseBody(View.LOGIN.getContents(), ContentType.TEXT_HTML_CHARSET_UTF_8);
    }
}

