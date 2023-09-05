package org.apache.coyote.http11.response;

import nextstep.jwp.LoginHandler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LoginPostResponseMaker extends ResponseMaker {

    @Override
    public String createResponse(final HttpRequest request) throws IOException {
        final LoginHandler loginHandler = new LoginHandler();
        if (loginHandler.login(request.getRequestBody())) {
            return successLoginResponse(request);
        }

        return failLoginResponse();
    }

    private String successLoginResponse(final HttpRequest request) {
        final HttpResponse httpResponse = new HttpResponse(StatusCode.FOUND);
        httpResponse.addJSessionId(request);
        return httpResponse.getRedirectResponse("/index.html");
    }

    private String failLoginResponse() throws IOException {
        final HttpResponse httpResponse = new HttpResponse(StatusCode.UNAUTHORIZED, ContentType.HTML, new String(getResponseBodyBytes("/401.html"), UTF_8));
        return httpResponse.getResponse();
    }

}
