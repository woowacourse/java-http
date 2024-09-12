package org.apache.coyote.http11.component.exceptionhandler;

import org.apache.coyote.http11.component.resource.StaticResourceFinder;
import org.apache.coyote.http11.component.response.HttpResponse;

public class UnAuthorizedExceptionHandler implements ExceptionHandler {

    private static final String UNAUTHORIZED_REDIRECT_URI = "http://localhost:8080/401.html";

    @Override
    public HttpResponse handle() {
        return StaticResourceFinder.renderRedirect(UNAUTHORIZED_REDIRECT_URI);
    }
}
