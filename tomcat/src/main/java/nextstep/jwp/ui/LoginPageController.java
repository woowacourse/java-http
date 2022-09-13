package nextstep.jwp.ui;

import nextstep.jwp.application.AuthService;
import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.mapping.controller.Controller;
import org.apache.coyote.http11.request.mapping.controllerscan.ControllerScan;
import org.apache.coyote.http11.request.mapping.controllerscan.RequestMapping;
import org.apache.coyote.http11.response.HtmlResponse;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.RedirectResponse;
import org.apache.coyote.http11.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerScan
public class LoginPageController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginPageController.class);

    private final AuthService authService = AuthService.getInstance();

    @Override
    @RequestMapping(method = HttpMethod.GET, uri = "/login")
    public HttpResponse handle(final HttpRequest httpRequest) {
        if (httpRequest.hasSession()) {
            final Session session = httpRequest.getSession()
                    .orElseThrow(() -> new UnauthorizedException("로그인된 세션을 찾을 수 없습니다."));
            return RedirectResponse.of("/index.html");
        }
        return HtmlResponse.of(HttpStatus.OK, HttpHeaders.empty(), "login");
    }
}
