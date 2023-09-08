package org.apache.coyote.http11.request;

import org.apache.coyote.http11.auth.AuthService;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.Location;
import org.apache.coyote.http11.response.ResponseEntity;

import static org.apache.coyote.http11.response.HttpStatus.OK;

public class RequestHandler {

    private final AuthService authService = new AuthService();

    public ResponseEntity getResponse(Request request) {
        final RequestLine requestLine = request.requestLine();
        final RequestHeader requestHeader = request.requestHeader();
        final RequestBody requestBody = request.requestBody();

        final Protocol protocol = request.requestLine().protocol();
        final String path = request.requestLine().path().defaultPath();

        if (path.equals("/login")) {
            return authService.login(requestLine, requestHeader, requestBody);
        }
        if (path.equals("/register")) {
            return authService.register(requestLine, requestBody);
        }
        return ResponseEntity.getCookieNullResponseEntity(protocol, OK, Location.from(path));
    }

}
