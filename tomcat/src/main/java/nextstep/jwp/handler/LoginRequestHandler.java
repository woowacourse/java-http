package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.exception.UncheckedServletException;
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

public class LoginRequestHandler implements HttpRequestHandler {

    private final HttpVersion httpVersion;

    public LoginRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    public HttpResponse handleHttpRequest(final HttpRequest httpRequest) {
        if (httpRequest.isGetMethod()) {
            return handleHttpGetRequest(httpRequest);
        }
        if (httpRequest.isPostMethod()) {
            return handleHttpPostRequest(httpRequest);
        }
        throw new UncheckedServletException("지원하는 method가 존재하지 않습니다.");
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
        HttpRequestBody httpRequestBody = httpRequest.getHttpRequestBody();
        String account = httpRequestBody.getValue("account");
        String password = httpRequestBody.getValue("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NotFoundUserException::new);

        HttpCookie httpCookie = HttpCookie.empty();
        if (user.checkPassword(password)) {
            Session session = Session.newSession();
            session.setAttribute("user", user);
            SessionManager.add(session);
            httpCookie.addSessionId(session.getId());
            return HttpResponse.found(httpVersion, httpCookie, new Location("/index.html"));
        }
        return HttpResponse.found(httpVersion, httpCookie, new Location("/401.html"));
    }

    private HttpResponse parseRequestFile(final HttpRequest httpRequest) {
        String responseBody = ResourcesUtil.readResource(httpRequest.getFilePath(), this.getClass());
        return HttpResponse.ok(httpVersion, HttpCookie.empty(), responseBody);
    }
}
