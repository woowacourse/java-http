package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.MemberService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseUtil;

public class RegisterHandler implements Handler {

    @Override
    public boolean supports(HttpRequest request) {
        return request.getUrl().equals("/register");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (doesRequestContainsFormdata(request)) {
            return MemberService.register(request);
        }
        String url = "/register.html";
        return ResponseUtil.buildStaticFileResponse(url);
    }

    private boolean doesRequestContainsFormdata(HttpRequest request) {
        return request.containsHeader("Content-Type") && request.getHeader("Content-Type")
                .contains("application/x-www-form-urlencoded");
    }
}
