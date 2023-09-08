package org.apache.coyote.http11.auth;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

public class RegisterController {

    private final RegisterService registerService = new RegisterService();
    public ResponseEntity register(Request request) {
        final RequestLine requestLine = request.requestLine();
        final RequestBody requestBody = request.requestBody();
        return registerService.register(requestLine, requestBody);
    }

}
