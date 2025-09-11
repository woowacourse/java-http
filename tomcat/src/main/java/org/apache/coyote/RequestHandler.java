package org.apache.coyote;

import com.techcourse.Service;
import java.io.IOException;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpResponseBuilder;
import org.apache.coyote.common.HttpStatus;

public class RequestHandler {

    private static final HttpResponseBuilder responseBuilder = new HttpResponseBuilder();
    private static final LoginHandler loginHandler = new LoginHandler(new HttpResponseBuilder(), new Service());
    private static final RegisterHandler registerHandler = new RegisterHandler(new HttpResponseBuilder(), new Service());

    public HttpResponse handle(final HttpRequest request) throws IOException {
        if (request.getPath().startsWith("/login")) {
            return loginHandler.handle(request);
        }
        if (request.getPath().startsWith("/register")) {
            return registerHandler.handle(request);
        }
        return responseBuilder.build(request, HttpStatus.FORBIDDEN, null, null);
    }
}
