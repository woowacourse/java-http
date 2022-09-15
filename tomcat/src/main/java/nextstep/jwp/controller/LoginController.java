package nextstep.jwp.controller;

import static org.apache.coyote.http11.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.HttpStatusCode.OK;
import static util.FileLoader.loadFile;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseBody;
import org.apache.coyote.http11.exception.unauthorized.LoginFailException;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Session session = request.getSession();
        if (session.isLoggedIn()) {
            response.statusCode(FOUND)
                    .redirect("/index.html");
            return;
        }
        final HttpResponseBody httpResponseBody = HttpResponseBody.ofFile(loadFile("/login.html"));
        response.statusCode(OK)
                .responseBody(httpResponseBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final HttpRequestBody httpRequestBody = request.getHttpRequestBody();
        final User user = login(httpRequestBody);
        final Session session = request.getSession();
        session.setAttribute("user", user);
        final HttpCookie httpCookie = HttpCookie.ofJSessionId(session.getId());
        response.statusCode(FOUND)
                .addCookie(httpCookie)
                .redirect("/index.html");
    }

    private User login(final HttpRequestBody httpRequestBody) {
        final String account = httpRequestBody.getBodyValue("account");
        final String password = httpRequestBody.getBodyValue("password");
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(LoginFailException::new);
        if (!user.checkPassword(password)) {
            throw new LoginFailException();
        }
        return user;
    }
}
