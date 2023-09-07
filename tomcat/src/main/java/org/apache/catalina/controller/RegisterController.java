package org.apache.catalina.controller;

import java.util.Map;
import nextstep.jwp.service.UserService;
import nextstep.mvc.ResponseWriter;
import org.apache.catalina.controller.config.RequestMapping;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.util.HttpParser;

@RequestMapping("/register")
public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final var body = httpRequest.getBody();
        Map<String, String> parameters = HttpParser.parseFormData(body);
        if (!parameters.containsKey("account") &&
                parameters.containsKey("password") &&
                parameters.containsKey("email")
        ) {
            ResponseWriter.redirect(httpResponse, "/400.html");
            return;
        }

        boolean result = UserService.register(parameters.get("account"),
                parameters.get("password"), parameters.get("email"));
        if (result) {
            ResponseWriter.redirect(httpResponse, "/index.html");
            return;
        }
        ResponseWriter.redirect(httpResponse, "/400.html");
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        ResponseWriter.view(httpResponse, HttpStatus.OK, httpRequest.getPath());
    }
}
