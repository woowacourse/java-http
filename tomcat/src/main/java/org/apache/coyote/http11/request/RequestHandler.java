package org.apache.coyote.http11.request;

import org.apache.coyote.http11.auth.LoginController;
import org.apache.coyote.http11.auth.RegisterController;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.response.Location;
import org.apache.coyote.http11.response.ResponseEntity;

import static org.apache.coyote.http11.response.HttpStatus.OK;

public class RequestHandler {

    private final RegisterController registerController = new RegisterController();
    private final LoginController loginController = new LoginController();

    public ResponseEntity getResponse(Request request) {
        final Protocol protocol = request.requestLine().protocol();
        final String path = request.requestLine().path().defaultPath();

        if (path.equals("/register")) {
            return registerController.register(request);
        }
        if (path.equals("/login")) {
            return loginController.login(request);
        }
        return ResponseEntity.getCookieNullResponseEntity(protocol, OK, Location.from(path));
    }

}
