package org.apache.coyote.http11.request;

import org.apache.coyote.http11.auth.LoginController;
import org.apache.coyote.http11.auth.RegisterController;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.response.Location;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.response.HttpStatus.OK;

public class RequestHandler {

    private final RegisterController registerController = new RegisterController();
    private final LoginController loginController = new LoginController();

    public HttpResponse getResponse(HttpRequest httpRequest) {
        final Protocol protocol = httpRequest.requestLine().protocol();
        final String path = httpRequest.requestLine().path().defaultPath();

        if (path.equals("/register")) {
            return registerController.register(httpRequest);
        }
        if (path.equals("/login")) {
            return loginController.login(httpRequest);
        }
        return HttpResponse.getCookieNullResponseEntity(protocol, OK, Location.from(path));
    }

}
