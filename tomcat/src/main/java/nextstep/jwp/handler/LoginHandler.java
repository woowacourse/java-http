package nextstep.jwp.handler;

import nextstep.jwp.exception.UnAuthorizationException;
import nextstep.jwp.exception.UnRegisteredUserException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.AuthService;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;
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

            final HttpHeaders headers = HttpHeaders.getEmptyHeaders();
            headers.put(HttpHeader.LOCATION, "/index.html");
            return new HttpResponse(HttpStatus.REDIRECT, headers);
        } catch (UnAuthorizationException | UnRegisteredUserException e) {
            final HttpHeaders headers = HttpHeaders.getEmptyHeaders();
            headers.put(HttpHeader.LOCATION, "/401.html");
            return new HttpResponse(HttpStatus.REDIRECT, headers);
        }
    }

    @Override
    public boolean isSupported(final HttpRequest request) {
        return request.isRequestMethodOf(HttpMethod.POST) &&
                request.isUrl(Url.from("/login")) &&
                request.hasBodyValueOf(ACCOUNT_QUERY_KEY) &&
                request.hasBodyValueOf(PASSWORD_QUERY_KEY);
    }
}
