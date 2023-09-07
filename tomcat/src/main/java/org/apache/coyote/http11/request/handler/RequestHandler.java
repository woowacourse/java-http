package org.apache.coyote.http11.request.handler;

import org.apache.coyote.http11.auth.AuthService;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.header.RequestHeader;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.Location;
import org.apache.coyote.http11.response.ResponseEntity;

import static org.apache.coyote.http11.response.HttpStatus.OK;

public class RequestHandler {

    private final AuthService authService = new AuthService();

    public ResponseEntity getResponse(
            RequestLine requestLine,
            RequestHeader requestHeader,
            RequestBody requestBody
    ) {
        Protocol protocol = requestLine.protocol();
        final String path = requestLine.path().defaultPath();
        if (path.equals("/login")) {
            return authService.login(requestLine, requestHeader, requestBody);
        }
        if (path.equals("/register")) {
            return authService.register(requestLine, requestBody);
        }
        return ResponseEntity.getCookieNullResponseEntity(protocol, OK, Location.from(path));
    }

}
