package nextstep.jwp.controller;

import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.response.HttpResponseHeader.LOCATION;
import static org.apache.coyote.http11.response.HttpResponseHeader.SET_COOKIE;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.util.QueryStringUtil;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String PATH = "/login";
    private static final String INDEX_PAGE_PATH = "/index.html";
    private static final String UNAUTHORIZED_PAGE_PATH = "/401.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String LOGIN_PAGE_VIEW_NAME = "login";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return PATH.equals(httpRequest.getPath());
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        try {
            final Map<String, String> data = QueryStringUtil.parse(request.getBody());
            checkQueryParameter(data);
            final String account = data.get(ACCOUNT);
            final String password = data.get(PASSWORD);
            final User user = findUser(account, password);
            log.info("login success! : user = {}", user);

            response.setStatusCode(StatusCode.FOUND);
            if (extractSessionId(request) == null) {
                final Session session = createSession(user);
                response.addHeader(SET_COOKIE, Session.getName() + "=" + session.getId());
            }
            response.addHeader(LOCATION, INDEX_PAGE_PATH);
        } catch (IllegalArgumentException exception) {
            log.info("login fail! : {}", exception.getMessage());
            response.setStatusCode(StatusCode.FOUND);
            response.addHeader(LOCATION, UNAUTHORIZED_PAGE_PATH);
        }
    }

    private void checkQueryParameter(final Map<String, String> data) {
        if (!data.containsKey(ACCOUNT) || !data.containsKey(PASSWORD)) {
            throw new IllegalArgumentException("쿼리 파라미터가 비었어요~");
        }
    }

    private User findUser(final String account, final String password) {
        final User findUser = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("그런 회원은 없어요 ~"));
        if (!findUser.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 틀렸어요 ~ ");
        }
        return findUser;
    }

    private String extractSessionId(final HttpRequest httpRequest) {
        final String cookieHeader = httpRequest.getHeaders().get(Cookie.getNAME());
        final Cookie cookie = Cookie.from(cookieHeader);
        return cookie.getAttribute(Session.getName());
    }

    private Session createSession(final User user) {
        final Session session = Session.create();
        session.setAttribute("user", user);
        SessionManager.add(session);
        return session;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final String sessionId = extractSessionId(request);
        if (sessionId == null) {
            resolveLoginPage(response);
            return;
        }
        final Session session = SessionManager.findSession(sessionId);
        if (session == null) {
            resolveLoginPage(response);
            return;
        }
        response.setStatusCode(StatusCode.FOUND);
        response.addHeader(LOCATION, INDEX_PAGE_PATH);
    }

    private void resolveLoginPage(final HttpResponse response) throws IOException {
        final String body = ViewResolver.findView(LOGIN_PAGE_VIEW_NAME);
        response.setStatusCode(StatusCode.OK);
        response.setBody(body);
        response.addHeader(CONTENT_TYPE, ContentType.HTML.getContentType());
        response.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }
}
