package org.apache.coyote.http11.handler.controller.register;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.handler.controller.base.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.Header;
import org.apache.coyote.http11.response.header.Status;

import java.util.Map;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) throws Exception {
        System.out.println("register");
        return super.service(httpRequest);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        return HttpResponse.okWithResource("/register.html");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) throws Exception {
        Map<String, String> params = httpRequest.getBody().getParams();
        userService.register(params.get("account"), params.get("password"), params.get("email"));
        return HttpResponse.found(ContentType.HTML, Status.FOUND, Map.of(Header.LOCATION, "/index.html"), "");
    }
}
