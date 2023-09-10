package org.apache.coyote.http11.auth;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController {

    private final LoginService loginService = new LoginService();

    public HttpResponse login(HttpRequest httpRequest) {
        final RequestLine requestLine = httpRequest.requestLine();
        final RequestHeader requestHeader = httpRequest.requestHeader();
        final RequestBody requestBody = httpRequest.requestBody();
        return loginService.login(requestLine, requestHeader, requestBody);
    }

}
