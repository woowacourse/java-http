package org.apache.coyote.http11.request.handler;

import org.apache.coyote.http11.auth.AuthService;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.headers.RequestHeader;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

import static org.apache.coyote.http11.response.HttpStatus.OK;

public class RequestHandler {

    private final AuthService authService = new AuthService();

    public ResponseEntity getResponse(
            RequestLine requestLine,
            RequestHeader requestHeader,
            RequestBody requestBody
    ) {
        final String path = requestLine.path().defaultPath();
        if (path.equals("/login")) {
            return authService.login(requestLine, requestHeader, requestBody);
        }
        if (path.equals("/register")) {
            return authService.register(requestLine, requestBody);
        }
        return new ResponseEntity(OK, path);
    }

}
