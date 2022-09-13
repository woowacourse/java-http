package nextstep.jwp.ui;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.mapping.controller.Controller;
import org.apache.coyote.http11.request.mapping.controllerscan.ControllerScan;
import org.apache.coyote.http11.request.mapping.controllerscan.RequestMapping;
import org.apache.coyote.http11.response.HtmlResponse;
import org.apache.coyote.http11.response.HttpResponse;

@ControllerScan
public class UserRegisterPageController implements Controller {

    @Override
    @RequestMapping(uri = "/register", method = HttpMethod.GET)
    public HttpResponse handle(final HttpRequest httpRequest) {
        return HtmlResponse.of(HttpStatus.OK, HttpHeaders.empty(), "register");
    }
}
