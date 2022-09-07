package nextstep.jwp.ui;

import java.util.Optional;
import nextstep.jwp.application.AuthService;
import nextstep.jwp.application.UserService;
import nextstep.jwp.application.dto.UserDto;
import nextstep.jwp.exception.LoginFailException;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryParams;
import org.apache.coyote.http11.request.RequestParams;
import org.apache.coyote.http11.request.mapping.controllerscan.Controller;
import org.apache.coyote.http11.request.mapping.controllerscan.RequestMapping;
import org.apache.coyote.http11.response.HtmlResponse;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.RedirectResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService = new UserService();
    private final AuthService authService = new AuthService();

    @RequestMapping(method = HttpMethod.GET, uri = "/login")
    public HttpResponse login(final HttpRequest httpRequest) {
        final RequestParams getRequestParams = httpRequest.getRequestParams();
        final Optional<String> account = getRequestParams.getValue("account");
        final Optional<String> password = getRequestParams.getValue("password");
        if (account.isEmpty() && password.isEmpty()) {
            return HtmlResponse.of(HttpStatus.OK, HttpHeaders.empty(), "login");
        }
        try {
            final String accountValue = account
                    .orElseThrow(LoginFailException::new);
            final String passwordValue = password
                    .orElseThrow(LoginFailException::new);
            final UserDto loginUser = authService.login(accountValue, passwordValue);
            return RedirectResponse.of("/index.html");
        } catch (final LoginFailException e) {
            return HtmlResponse.of(HttpStatus.UNAUTHORIZED, HttpHeaders.empty(), "401");
        }
    }
}
