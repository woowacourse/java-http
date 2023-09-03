package org.apache.coyote.handler.get;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.Headers;
import org.apache.coyote.handler.RequestHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.QueryParams;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.util.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

import static org.apache.coyote.common.CharacterSet.UTF_8;
import static org.apache.coyote.common.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.common.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.common.HttpHeader.LOCATION;
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
        final QueryParams queryParams = httpRequest.queryParams();
        if (queryParams.isEmpty()) {
            return responseLoginPage();
        }

        final String account = queryParams.getParamValue("account");
        final String password = queryParams.getParamValue("password");
        final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(account);
        if (maybeUser.isPresent() && maybeUser.get().checkPassword(password)) {
            return redirectHomePage(maybeUser);
        }

        return redirect401Page();
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

    private HttpResponse redirectHomePage(final Optional<User> maybeUser) {
        log.info("===========> 로그인된 유저 = {}", maybeUser.get());

        final Headers successResponseHeader = new Headers(Map.of(
                CONTENT_TYPE.source(), TEXT_HTML.source() + ";" + UTF_8.source(),
                LOCATION.source(), LOGIN_SUCCESS_REDIRECT_URI
        ));
        return new HttpResponse(HTTP_1_1, FOUND, successResponseHeader, ResponseBody.EMPTY);
    }

    private HttpResponse redirect401Page() {
        final Headers failResponseHeader = new Headers(Map.of(
                CONTENT_TYPE.source(), TEXT_HTML.source() + ";" + UTF_8.source(),
                LOCATION.source(), LOGIN_FAIL_REDIRECT_URI
        ));
        return new HttpResponse(HTTP_1_1, FOUND, failResponseHeader, ResponseBody.EMPTY);
    }
}
