package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseFactory;
import org.apache.coyote.http11.HttpResponseStatusLine;

public class LoginController extends RestController {

    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String SUPPORTED_CONTENT_TYPE = "application/x-www-form-urlencoded";

    public LoginController() {
        super("/login");
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        return login(httpRequest);
    }

    private HttpResponse login(final HttpRequest httpRequest) {
        try {
            final Map<String, String> body = httpRequest.getBody();
            final String account = body.get("account");
            final String password = body.get("password");

            final User user = findUser(account);
            validatePassword(user, password);

            return makeSuccessResponse(httpRequest);
        } catch (IllegalArgumentException e) {
            return HttpResponseFactory.createRedirectHttpResponse(HttpResponseStatusLine.FOUND(), UNAUTHORIZED_PAGE);
        }
    }

    private User findUser(final String account) {
        return InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
    }

    private void validatePassword(final User user, final String password) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private HttpResponse makeSuccessResponse(final HttpRequest httpRequest) {
        if (!httpRequest.containsCookieAndJSessionID()) {
            final HttpCookie httpCookie = HttpCookie.createJSessionID();
            return HttpResponseFactory.createRedirectHttpResponse(HttpResponseStatusLine.FOUND(), INDEX_PAGE,
                httpCookie);
        }
        return HttpResponseFactory.createRedirectHttpResponse(HttpResponseStatusLine.FOUND(), INDEX_PAGE);
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final boolean isPostMethod = httpRequest.getMethod() == HttpMethod.POST;
        final boolean isSupportedContentType = httpRequest.containsContentType(SUPPORTED_CONTENT_TYPE);

        return super.canHandle(httpRequest) && isPostMethod && isSupportedContentType;
    }
}
