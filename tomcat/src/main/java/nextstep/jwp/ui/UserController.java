package nextstep.jwp.ui;

import nextstep.jwp.application.UserService;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestParams;
import org.apache.coyote.http11.request.mapping.controllerscan.Controller;
import org.apache.coyote.http11.request.mapping.controllerscan.RequestMapping;
import org.apache.coyote.http11.response.HtmlResponse;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.RedirectResponse;

@Controller
public class UserController {

    private final UserService userService = UserService.getInstance();

    @RequestMapping(uri = "/register", method = HttpMethod.GET)
    public HttpResponse registerPage(final HttpRequest httpRequest) {
        return HtmlResponse.of(HttpStatus.OK, HttpHeaders.empty(), "register");
    }

    @RequestMapping(uri = "/register", method = HttpMethod.POST)
    public HttpResponse register(final HttpRequest httpRequest) {
        final RequestParams requestParams = httpRequest.getRequestParams();
        try {
            final String account = requestParams.getValue("account")
                    .orElseThrow(() -> new BadRequestException("account 파라미터가 존재하지 않습니다."));
            final String password = requestParams.getValue("password")
                    .orElseThrow(() -> new BadRequestException("password 파라미터가 존재하지 않습니다."));
            final String email = requestParams.getValue("email")
                    .orElseThrow(() -> new BadRequestException("email 파라미터가 존재하지 않습니다."));

            userService.save(account, password, email);
            return RedirectResponse.of("/index.html");
        } catch (final BadRequestException e) {
            return RedirectResponse.of("/400.html");
        }
    }
}
