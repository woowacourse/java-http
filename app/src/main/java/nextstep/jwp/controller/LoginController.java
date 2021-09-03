package nextstep.jwp.controller;

import static nextstep.jwp.controller.DefaultController.INDEX_RESOURCE_PATH;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.requestline.RequestPath;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.http.session.HttpCookie;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;
import nextstep.jwp.http.util.ParamExtractor;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final String LOGIN_RESOURCE_PATH = "/login.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        Object user = request.getSession().getAttribute("user");
        if (Objects.nonNull(user)) {
            return HttpResponse.redirect(INDEX_RESOURCE_PATH);
        }
        return HttpResponse.of(HttpStatus.OK, new RequestPath(LOGIN_RESOURCE_PATH));
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        try {
            final Body body = request.getBody();
            final Map<String, String> params = ParamExtractor.extractParams(body.asString());
            String account = params.get("account");
            String password = params.get("password");

            final Optional<User> user = InMemoryUserRepository.findByAccount(account);
            if (user.isPresent()) {
                User presentUser = user.get();

                if (presentUser.checkPassword(password)) {
                    final HttpSession httpSession = request.getSession();
                    httpSession.setAttribute("user", user);
                    return HttpResponse.redirect(INDEX_RESOURCE_PATH);
                }
                throw new UnauthorizedException();
            }

            String sessionId = request.getSession().getId();
            if (!HttpSessions.isValid(sessionId)) {
                HttpCookie cookie = new HttpCookie(Map.of("JSESSIONID", sessionId));
                cookie.addCookie(sessionId);
                // response에 Set-Cookie 설정
                HttpSessions.putSession(request.getSession());
            }

        } catch (UnauthorizedException e) {
            return HttpResponse.redirect(UNAUTHORIZED_PATH);
        }
    }
}
