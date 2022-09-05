package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import nextstep.jwp.exception.UnsupportedMethodException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.catalina.session.Session;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.message.request.QueryParams;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.header.Cookie;
import org.apache.coyote.http11.message.request.requestline.Method;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.header.StatusCode;

public class LoginController implements Controller {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_PASSWORD = "password";
    private static final String PATH_REDIRECT = "/index.html";

    private static HttpResponse doGet(HttpRequest httpRequest) throws IOException, URISyntaxException {
        if (hasLoggedIn(httpRequest)) {
            return HttpResponse.ofRedirection(StatusCode.FOUND, PATH_REDIRECT);
        }

        return HttpResponse.ofResource("/login.html");
    }

    private static boolean hasLoggedIn(final HttpRequest httpRequest) {
        final Cookie cookie = httpRequest.getCookie();
        final Optional<String> jSessionId = cookie.getJSessionId();
        if (jSessionId.isEmpty()) {
            return false;
        }

        final Session session = SESSION_MANAGER.findSession(jSessionId.get());
        return session != null && session.getAttribute("user") != null;
    }

    private static HttpResponse doPost(final HttpRequest httpRequest) {
        final QueryParams requestParams = httpRequest.getBodyQueryParams();
        checkParams(requestParams);

        final User user = UserService.login(requestParams.get(KEY_ACCOUNT), requestParams.get(KEY_PASSWORD));
        final Session session = createSession(user);
        SESSION_MANAGER.add(session);

        final HttpResponse httpResponse = HttpResponse.ofRedirection(StatusCode.FOUND, PATH_REDIRECT);
        httpResponse.setCookie(Cookie.fromJSessionId(session.getId()));
        return httpResponse;
    }

    private static void checkParams(final QueryParams queryParams) {
        if (!queryParams.containsKey(KEY_ACCOUNT) || !queryParams.containsKey(KEY_PASSWORD)) {
            throw new IllegalArgumentException("계정과 비밀번호를 입력하세요.");
        }
    }

    private static Session createSession(final User user) {
        final Session session = new Session();
        session.setAttribute("user", user);
        return session;
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) throws Exception {
        if (httpRequest.isMethod(Method.GET)) {
            return doGet(httpRequest);
        }

        if (httpRequest.isMethod(Method.POST)) {
            return doPost(httpRequest);
        }

        throw new UnsupportedMethodException();
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return httpRequest.isPath("/login");
    }
}
