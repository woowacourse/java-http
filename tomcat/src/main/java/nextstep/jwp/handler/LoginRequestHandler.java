package nextstep.jwp.handler;

import nextstep.jwp.application.LoginService;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.Location;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.HttpRequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.session.Session;
import nextstep.jwp.http.session.SessionManager;
import nextstep.jwp.model.User;

public final class LoginRequestHandler extends AbstractHttpRequestHandler {

    private final LoginService loginService = new LoginService();

    public LoginRequestHandler(final HttpVersion httpVersion) {
        super(httpVersion);
    }

    @Override
    protected HttpResponse handleHttpGetRequest(final HttpRequest httpRequest) {
        if (!httpRequest.isEmptySessionId()) {
            return SessionManager.findSession(httpRequest.getJsessionId())
                    .map(session -> HttpResponse.found(httpVersion, HttpCookie.empty(), new Location("/index.html")))
                    .orElse(handleStaticResourceRequest(httpRequest));
        }
        return handleStaticResourceRequest(httpRequest);
    }

    @Override
    protected HttpResponse handleHttpPostRequest(final HttpRequest httpRequest) {
        HttpCookie httpCookie = HttpCookie.empty();
        HttpRequestBody httpRequestBody = httpRequest.getHttpRequestBody();

        User user = loginService.findUser(httpRequestBody.getValue("account"));

        if (user.checkPassword(httpRequestBody.getValue("password"))) {
            return loginUserRedirect(user, httpCookie);
        }
        return HttpResponse.found(httpVersion, httpCookie, new Location("/401.html"));
    }

    private HttpResponse loginUserRedirect(final User user, final HttpCookie httpCookie) {
        Session session = Session.newSession();
        session.setAttribute("user", user);
        SessionManager.add(session);
        httpCookie.addSessionId(session.getId());
        return HttpResponse.found(httpVersion, httpCookie, new Location("/index.html"));
    }
}
