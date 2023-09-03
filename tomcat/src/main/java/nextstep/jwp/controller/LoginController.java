package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestURI;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseFactory;
import org.apache.coyote.http11.HttpResponseStatusLine;
import org.apache.coyote.http11.QueryStrings;

public class LoginController extends RestController {

    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    public LoginController() {
        super("/login");
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        return login(httpRequest.getQueryStrings());
    }

    private HttpResponse login(final QueryStrings queryStrings) {
        try {
            final String account = queryStrings.getValueByName("account");
            final String password = queryStrings.getValueByName("password");

            final User user = findUser(account);
            validatePassword(user, password);

            return HttpResponseFactory.createRedirectHttpResponse(HttpResponseStatusLine.FOUND(), INDEX_PAGE);
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

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final HttpRequestURI httpRequestURI = httpRequest.getRequestURI();
        return super.canHandle(httpRequest) && httpRequestURI.hasQueryString();
    }
}
