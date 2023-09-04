package nextstep.jwp.handler;

import nextstep.jwp.exception.AlreadyRegisteredUserException;
import nextstep.jwp.service.AuthService;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;
import org.apache.coyote.http.vo.Url;

public class RegisterHandler implements Handler {

    private static final String ACCOUNT_BODY = "account";
    private static final String EMAIL_BODY = "email";
    private static final String PASSWORD_BODY = "password";
    private final AuthService authService = new AuthService();

    @Override
    public HttpResponse handle(final HttpRequest request) {
        try {
            authService.register(
                    request.getBodyValueOf(ACCOUNT_BODY),
                    request.getBodyValueOf(EMAIL_BODY),
                    request.getBodyValueOf(PASSWORD_BODY)
            );

            final HttpHeaders headers = HttpHeaders.getEmptyHeaders();
            headers.put(HttpHeader.LOCATION, "/index.html");

            return new HttpResponse.Builder()
                    .status(HttpStatus.REDIRECT)
                    .headers(headers)
                    .build();

        } catch (AlreadyRegisteredUserException e) {
            final HttpHeaders headers = HttpHeaders.getEmptyHeaders();

            return new HttpResponse.Builder().status(HttpStatus.BAD_REQUEST)
                    .headers(headers)
                    .body("중복된 계정입니다.")
                    .build();
        }
    }

    @Override
    public boolean isSupported(final HttpRequest request) {
        return request.isRequestMethodOf(HttpMethod.POST) &&
                request.isUrl(Url.from("/register")) &&
                request.hasBodyValueOf("account") &&
                request.hasBodyValueOf("email") &&
                request.hasBodyValueOf("password");
    }
}
