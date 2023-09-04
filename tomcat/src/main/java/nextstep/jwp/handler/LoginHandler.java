package nextstep.jwp.handler;

import java.util.UUID;
import nextstep.jwp.exception.UnAuthorizationException;
import nextstep.jwp.exception.UnRegisteredUserException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.AuthService;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.vo.Cookie;
import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;
import org.apache.coyote.http.vo.HttpResponse.Builder;
import org.apache.coyote.http.vo.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static final String ACCOUNT_QUERY_KEY = "account";
    private static final String PASSWORD_QUERY_KEY = "password";

    private final AuthService authService = new AuthService();

    @Override
    public HttpResponse handle(final HttpRequest request) {
        try {
            final String account = request.getBodyValueOf(ACCOUNT_QUERY_KEY);
            final String password = request.getBodyValueOf(PASSWORD_QUERY_KEY);
            final User user = authService.login(account, password);
            log.info("User ={}", user);

            final HttpResponse.Builder responseBuilder = getHttpResponse("/index.html");

            if (!request.hasCookie("JSESSIONID")) {
                return createCookieResponse(user, responseBuilder);
            }

            return responseBuilder.build();

        } catch (UnAuthorizationException | UnRegisteredUserException e) {
            return getHttpResponse("/401.html").build();
        }
    }

    private HttpResponse createCookieResponse(final User user, final Builder responseBuilder) {
        final Cookie cookie = createCookie(user);
        return responseBuilder
                .cookie(cookie)
                .build();
    }

    private Cookie createCookie(final User user) {
        final Cookie cookie = Cookie.emptyCookie();
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        SessionManager.add(session.getId(), session);
        cookie.put("JSESSIONID", session.getId());
        return cookie;
    }

    private HttpResponse.Builder getHttpResponse(final String value) {
        final HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        headers.put(HttpHeader.LOCATION, value);
        return new HttpResponse.Builder()
                .status(HttpStatus.REDIRECT)
                .headers(headers);
    }

    @Override
    public boolean isSupported(final HttpRequest request) {
        return request.isRequestMethodOf(HttpMethod.POST) &&
                request.isUrl(Url.from("/login")) &&
                request.hasBodyValueOf(ACCOUNT_QUERY_KEY) &&
                request.hasBodyValueOf(PASSWORD_QUERY_KEY);
    }
}
