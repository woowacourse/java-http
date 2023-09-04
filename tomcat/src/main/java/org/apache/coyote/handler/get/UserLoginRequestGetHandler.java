package org.apache.coyote.handler.get;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.session.Cookies;
import org.apache.coyote.common.Headers;
import org.apache.coyote.handler.RequestHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.QueryParams;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.apache.coyote.util.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.apache.coyote.common.CharacterSet.UTF_8;
import static org.apache.coyote.common.HeaderType.CONTENT_LENGTH;
import static org.apache.coyote.common.HeaderType.CONTENT_TYPE;
import static org.apache.coyote.common.HeaderType.LOCATION;
import static org.apache.coyote.common.HeaderType.SET_COOKIE;
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
        final QueryParams queryParams = httpRequest.requestLine().queryParams();
        if (isLogined(httpRequest)) {
            return redirectHomePage(httpRequest);
        }

        if (queryParams.isEmpty()) {
            return responseLoginPage();
        }
        

        final String account = queryParams.getParamValue("account");
        final String password = queryParams.getParamValue("password");
        final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(account);
        if (maybeUser.isPresent() && maybeUser.get().checkPassword(password)) {
            log.info("===========> 로그인된 유저 = {}", maybeUser.get());
            return redirectHomePage(httpRequest);
        }

        return redirect401Page();
    }

    private boolean isLogined(final HttpRequest httpRequest) {
        final Cookies cookies = httpRequest.cookies();
        final Session foundSession = SessionManager.findSession(cookies.getCookieValue("JSESSIONID"));

        return Objects.nonNull(foundSession);
    }

    private HttpResponse responseLoginPage() {
        final String loginPageResource = ResourceReader.read(LOGIN_PAGE_URI);
        final ResponseBody loginPageBody = new ResponseBody(loginPageResource);

        final Headers loginPageResponseHeader = new Headers(Map.of(
                CONTENT_TYPE.source(), TEXT_HTML.source() + ";" + UTF_8.source(),
                CONTENT_LENGTH.source(), String.valueOf(loginPageBody.length())
        ));

        return new HttpResponse(HTTP_1_1, OK, loginPageResponseHeader, loginPageBody);
    }

    private HttpResponse redirectHomePage(final HttpRequest httpRequest) {
        final Map<String, String> responseHeaders = new HashMap<>();


        responseHeaders.put(CONTENT_TYPE.source(), TEXT_HTML.source() + ";" + UTF_8.source());
        responseHeaders.put(CONTENT_LENGTH.source(), String.valueOf(ResponseBody.EMPTY.length()));
        responseHeaders.put(LOCATION.source(), LOGIN_SUCCESS_REDIRECT_URI);
        final Session newSession = new Session(UUID.randomUUID().toString());
        responseHeaders.put(SET_COOKIE.source(), "JSESSIONID=" + newSession.id());
        SessionManager.add(newSession);

        final Headers successResponseHeader = new Headers(responseHeaders);

        return new HttpResponse(HTTP_1_1, FOUND, successResponseHeader, ResponseBody.EMPTY);
    }

    private HttpResponse redirect401Page() {
        final Headers failResponseHeader = new Headers(Map.of(
                CONTENT_TYPE.source(), TEXT_HTML.source() + ";" + UTF_8.source(),
                CONTENT_LENGTH.source(), String.valueOf(ResponseBody.EMPTY.length()),
                LOCATION.source(), LOGIN_FAIL_REDIRECT_URI
        ));
        return new HttpResponse(HTTP_1_1, FOUND, failResponseHeader, ResponseBody.EMPTY);
    }
}
