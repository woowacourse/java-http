package nextstep.jwp.ui;

import nextstep.jwp.application.UserService;
import nextstep.jwp.application.dto.UserDto;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryParams;
import org.apache.coyote.http11.request.mapping.controllerscan.Controller;
import org.apache.coyote.http11.request.mapping.controllerscan.RequestMapping;
import org.apache.coyote.http11.response.HtmlResponse;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService = new UserService();

    @RequestMapping(method = HttpMethod.GET, uri = "/login")
    public HttpResponse login(final HttpRequest httpRequest) {
        final QueryParams queryParams = httpRequest.getQueryParams();
        final String account = queryParams.getValue("account")
                .orElseThrow(() -> new BadRequestException("account가 없습니다."));
        final String password = queryParams.getValue("password")
                .orElseThrow(() -> new BadRequestException("password가 없습니다."));
        final UserDto user = userService.findByAccount(account);
        log.info("user = {}", user);
        return HtmlResponse.of(HttpStatus.OK, HttpHeaders.empty(), "login.html");
    }
}
