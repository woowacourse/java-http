package org.apache.coyote.http11.auth;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

public class LoginController {

    private final LoginService loginService = new LoginService();

    public ResponseEntity login(Request request) {
        final RequestLine requestLine = request.requestLine();
        final RequestHeader requestHeader = request.requestHeader();
        final RequestBody requestBody = request.requestBody();
        return loginService.login(requestLine, requestHeader, requestBody);
    }

}
