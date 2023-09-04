package org.apache.coyote.handler.get;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.RequestHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.QueryParams;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.session.Cookies;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.apache.coyote.util.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.apache.coyote.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.common.MediaType.TEXT_HTML;
import static org.apache.coyote.response.HttpStatus.FOUND;
import static org.apache.coyote.response.HttpStatus.OK;

public class UserLoginRequestGetHandler implements RequestHandler {

    private static final String LOGIN_PAGE_URI = "/login.html";
    private static final String LOGIN_SUCCESS_REDIRECT_URI = "/index.html";
    private static final String LOGIN_FAIL_REDIRECT_URI = "/401.html";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        if (isAuthenticated(httpRequest)) {
            return redirectHomePageWhenAuthenticated();
        }

        final QueryParams queryParams = httpRequest.requestLine().queryParams();
        if (queryParams.isEmpty()) {
            return responseLoginPage();
        }

        final String account = queryParams.getParamValue("account");
        final String password = queryParams.getParamValue("password");
        final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(account);
        if (maybeUser.isPresent() && maybeUser.get().checkPassword(password)) {
            log.info("===========> 로그인된 유저 = {}", maybeUser.get());
            return redirectHomePageWhenNewAuthentication(maybeUser.get());
        }

        return redirect401PageWhenFailed();
    }

    private boolean isAuthenticated(final HttpRequest httpRequest) {
        final Session foundSession = SessionManager.findSession(httpRequest.getCookieValue("JSESSIONID"));

        return Objects.nonNull(foundSession);
    }

    private HttpResponse redirectHomePageWhenAuthenticated() {
        return HttpResponse.builder()
                .setHttpVersion(HTTP_1_1)
                .setHttpStatus(FOUND)
                .sendRedirect(LOGIN_SUCCESS_REDIRECT_URI)
                .build();
    }

    private HttpResponse responseLoginPage() {
        final String loginPageResource = ResourceReader.read(LOGIN_PAGE_URI);
        final ResponseBody loginPageBody = new ResponseBody(loginPageResource);

        return HttpResponse.builder()
                .setHttpVersion(HTTP_1_1)
                .setHttpStatus(OK)
                .setContentType(TEXT_HTML.source())
                .setContentLength(loginPageBody.length())
                .setResponseBody(loginPageBody)
                .build();
    }

    private HttpResponse redirectHomePageWhenNewAuthentication(final User loginUser) {
        final Session newSession = new Session(UUID.randomUUID().toString());
        newSession.setAttribute("account", loginUser.getAccount());
        SessionManager.add(newSession);

        return HttpResponse.builder()
                .setHttpVersion(HTTP_1_1)
                .setCookies(Cookies.ofJSessionId(newSession.id()))
                .sendRedirect(LOGIN_SUCCESS_REDIRECT_URI)
                .build();
    }

    private HttpResponse redirect401PageWhenFailed() {
        return HttpResponse.builder()
                .setHttpVersion(HTTP_1_1)
                .setHttpStatus(FOUND)
                .sendRedirect(LOGIN_FAIL_REDIRECT_URI)
                .build();
    }
}
