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
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.request.header.Cookie;
import org.apache.coyote.http11.message.request.requestline.Method;
import org.apache.coyote.http11.message.response.Response;
import org.apache.coyote.http11.message.response.header.StatusCode;

public class LoginController implements Controller {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_PASSWORD = "password";
    private static final String PATH_REDIRECT = "/index.html";

    private static Response doGet(Request request) throws IOException, URISyntaxException {
        if (hasLoggedIn(request)) {
            return Response.ofRedirection(StatusCode.FOUND, PATH_REDIRECT);
        }

        return Response.ofResource("/login.html");
    }

    private static boolean hasLoggedIn(final Request request) {
        final Cookie cookie = request.getCookie();
        final Optional<String> jSessionId = cookie.getJSessionId();
        if (jSessionId.isEmpty()) {
            return false;
        }

        final Session session = SESSION_MANAGER.findSession(jSessionId.get());
        return session != null && session.getAttribute("user") != null;
    }

    private static Response doPost(final Request request) {
        final QueryParams requestParams = request.getBodyQueryParams();
        checkParams(requestParams);

        final User user = UserService.login(requestParams.get(KEY_ACCOUNT), requestParams.get(KEY_PASSWORD));
        final Session session = createSession(user);
        SESSION_MANAGER.add(session);

        final Response response = Response.ofRedirection(StatusCode.FOUND, PATH_REDIRECT);
        response.setCookie(Cookie.fromJSessionId(session.getId()));
        return response;
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
    public Response service(final Request request) throws Exception {
        if (request.isMethod(Method.GET)) {
            return doGet(request);
        }

        if (request.isMethod(Method.POST)) {
            return doPost(request);
        }

        throw new UnsupportedMethodException();
    }

    @Override
    public boolean canHandle(final Request request) {
        return request.isPath("/login");
    }
}
