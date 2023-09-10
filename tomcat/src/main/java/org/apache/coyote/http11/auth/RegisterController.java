package org.apache.coyote.http11.auth;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController {

    private final RegisterService registerService = new RegisterService();
    public HttpResponse register(HttpRequest httpRequest) {
        final RequestLine requestLine = httpRequest.requestLine();
        final RequestBody requestBody = httpRequest.requestBody();
        return registerService.register(requestLine, requestBody);
    }

}
