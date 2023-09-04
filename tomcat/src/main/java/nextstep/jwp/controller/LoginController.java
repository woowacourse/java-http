package nextstep.jwp.controller;

import static org.apache.coyote.http11.StaticPages.INDEX_PAGE;
import static org.apache.coyote.http11.StaticPages.UNAUTHORIZED_PAGE;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponseStatusLine;
import org.apache.coyote.http11.ResponseEntityFactory;
import org.apache.coyote.http11.Session;

public class LoginController extends RestController {

    private static final String SUPPORTED_CONTENT_TYPE = "application/x-www-form-urlencoded";

    public LoginController() {
        super("/login");
    }

    private static Session createSession(final User user) {
        final Session session = new Session();
        SessionManager.getInstance().add(session);
        session.setAttribute("user", user);
        return session;
    }

    @Override
    public ResponseEntity service(final HttpRequest httpRequest) {
        return login(httpRequest);
    }

    private ResponseEntity login(final HttpRequest httpRequest) {
        try {
            final Map<String, String> body = httpRequest.getBody();
            final String account = body.get("account");
            final String password = body.get("password");

            final User user = findUser(account);
            validatePassword(user, password);

            return makeSuccessResponse(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntityFactory.createRedirectHttpResponse(HttpResponseStatusLine.FOUND(), UNAUTHORIZED_PAGE);
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

    private ResponseEntity makeSuccessResponse(final User user) {
        final Session session = createSession(user);
        final HttpCookie httpCookie = HttpCookie.fromJSessionId(session.getId());
        return ResponseEntityFactory.createRedirectHttpResponse(HttpResponseStatusLine.FOUND(), INDEX_PAGE,
            httpCookie);
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final boolean isPostMethod = httpRequest.getMethod() == HttpMethod.POST;
        final boolean isSupportedContentType = httpRequest.containsContentType(SUPPORTED_CONTENT_TYPE);

        return super.canHandle(httpRequest) && isPostMethod && isSupportedContentType;
    }
}
