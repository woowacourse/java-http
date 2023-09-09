package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.QueryParams;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.session.Cookies;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.apache.coyote.common.HttpVersion.HTTP_1_1;

public class LoginController extends AbstractController {

    private static final String LOGIN_SUCCESS_REDIRECT_URI = "/index.html";
    private static final String LOGIN_FAIL_REDIRECT_URI = "/401.html";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isAuthenticated(request)) {
            response.setHttpVersion(HTTP_1_1)
                    .sendRedirect(LOGIN_SUCCESS_REDIRECT_URI)
                    .setResponseBody(ResponseBody.empty());

            return;
        }

        final QueryParams queryParams = request.queryParams();
        final String account = queryParams.getParamValue("account");
        final String password = queryParams.getParamValue("password");

        final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(account);
        if (maybeUser.isPresent() && maybeUser.get().checkPassword(password)) {
            log.info("===========> 로그인된 유저 = {}", maybeUser.get());
            final Session newSession = new Session(UUID.randomUUID().toString());
            newSession.setAttribute("account", maybeUser.get().getAccount());
            SessionManager.add(newSession);

            response.setHttpVersion(HTTP_1_1)
                    .setCookies(Cookies.ofJSessionId(newSession.id()))
                    .sendRedirect(LOGIN_SUCCESS_REDIRECT_URI);

            return;
        }

        response.setHttpVersion(HTTP_1_1)
                .sendRedirect(LOGIN_FAIL_REDIRECT_URI);
    }

    private boolean isAuthenticated(final HttpRequest httpRequest) {
        final Session foundSession = SessionManager.findSession(httpRequest.getCookieValue("JSESSIONID"));

        return Objects.nonNull(foundSession);
    }
}
