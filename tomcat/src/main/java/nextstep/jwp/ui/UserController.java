package nextstep.jwp.ui;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.mapping.controllerscan.Controller;
import org.apache.coyote.http11.request.mapping.controllerscan.RequestMapping;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.RedirectResponse;

@Controller
public class UserController {

    @RequestMapping(uri = "/register", method = HttpMethod.POST)
    public HttpResponse register(final HttpRequest httpRequest) {

        System.out.println(httpRequest.getRequestParams().getValue("account").get());
        System.out.println(httpRequest.getRequestParams().getValue("password").get());
        System.out.println(httpRequest.getRequestParams().getValue("email").get());
        return RedirectResponse.of("/index.html");
    }
}
