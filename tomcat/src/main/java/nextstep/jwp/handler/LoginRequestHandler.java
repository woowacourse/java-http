package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpRequestBody;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.Location;
import nextstep.jwp.http.Session;
import nextstep.jwp.http.SessionManager;
import nextstep.jwp.model.User;
import nextstep.jwp.util.ResourcesUtil;

public final class LoginRequestHandler extends AbstractHttpRequestHandler {

    private final HttpVersion httpVersion;

    public LoginRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    public HttpResponse handleHttpGetRequest(final HttpRequest httpRequest) {
        if (!httpRequest.isEmptySessionId()) {
            return SessionManager.findSession(httpRequest.getJsessionId())
                    .map(session -> HttpResponse.found(httpVersion, HttpCookie.empty(), new Location("/index.html")))
                    .orElse(parseRequestFile(httpRequest));
        }
        return parseRequestFile(httpRequest);
    }

    @Override
    public HttpResponse handleHttpPostRequest(final HttpRequest httpRequest) {
        HttpCookie httpCookie = HttpCookie.empty();
        HttpRequestBody httpRequestBody = httpRequest.getHttpRequestBody();

        User user = findUser(httpRequestBody);

        if (user.checkPassword(httpRequestBody.getValue("password"))) {
            return loginUserRedirect(user, httpCookie);
        }
        return HttpResponse.found(httpVersion, httpCookie, new Location("/401.html"));
    }

    private HttpResponse parseRequestFile(final HttpRequest httpRequest) {
        String responseBody = ResourcesUtil.readResource(httpRequest.getFilePath(), this.getClass());
        return HttpResponse.ok(httpVersion, HttpCookie.empty(), responseBody);
    }

    private User findUser(final HttpRequestBody httpRequestBody) {
        String account = httpRequestBody.getValue("account");
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NotFoundUserException::new);
    }

    private HttpResponse loginUserRedirect(final User user, final HttpCookie httpCookie) {
        Session session = Session.newSession();
        session.setAttribute("user", user);
        SessionManager.add(session);
        httpCookie.addSessionId(session.getId());
        return HttpResponse.found(httpVersion, httpCookie, new Location("/index.html"));
    }
}
