package org.apache.coyote;

import com.techcourse.Service;
import java.io.IOException;

public class RequestHandler {

    private static final ResponseBuilder responseBuilder = new ResponseBuilder();
    private static final LoginHandler loginHandler = new LoginHandler(new ResponseBuilder(), new Service());
    private static final RegisterHandler registerHandler = new RegisterHandler(new ResponseBuilder(), new Service());

    public byte[] handle(final HttpRequest request) throws IOException {
        if (request.uri().startsWith("/login")) {
            return loginHandler.handle(request);
        }
        if (request.uri().startsWith("/register")) {
            return registerHandler.handle(request);
        }
        return responseBuilder.build(null, HttpStatus.FORBIDDEN, null, null);
    }
}
