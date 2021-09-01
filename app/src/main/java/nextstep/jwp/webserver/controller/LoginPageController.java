package nextstep.jwp.webserver.controller;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

import nextstep.jwp.framework.context.AbstractController;
import nextstep.jwp.framework.http.*;
import nextstep.jwp.framework.http.template.RedirectResponseTemplate;
import nextstep.jwp.framework.http.template.ResourceResponseTemplate;
import nextstep.jwp.webserver.db.InMemoryUserRepository;
import nextstep.jwp.webserver.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPageController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginPageController.class);

    public LoginPageController() {
        super("/login", EnumSet.of(HttpMethod.GET, HttpMethod.POST));
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        final HttpSession session = httpRequest.getSession();
        final Object user = session.getAttribute("user");
        if (isLoggedIn(user)) {
            LOGGER.debug("로그인 성공!! - 세션");
            return new RedirectResponseTemplate().found("/index.html");
        }

        return new ResourceResponseTemplate().ok("/login.html");
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest) {
        final Query query = new Query(httpRequest.getBody());
        final String account = query.get("account");
        final String password = query.get("password");

        LOGGER.debug("로그인 요청 - [account : {}, password : {}]", account, password);

        final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty() || !optionalUser.get().checkPassword(password)) {
            LOGGER.debug("로그인 실패!!");
            return new ResourceResponseTemplate().unauthorized("/401.html");
        }

        LOGGER.debug("로그인 성공!!");
        final HttpSession session = httpRequest.getSession();
        session.setAttribute("user", optionalUser.get());

        final Cookie cookie = new Cookie(HttpSession.JSESSIONID, session.getId());
        final HttpHeaders httpHeaders = HttpHeaders.of(HttpHeaders.SET_COOKIE, cookie.toString());
        return new RedirectResponseTemplate().found("/index.html", httpHeaders);
    }

    private boolean isLoggedIn(Object user) {
        return Objects.nonNull(user);
    }
}
