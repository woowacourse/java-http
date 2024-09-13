package org.apache.catalina.servlet.exceptionhandler;

import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.resource.StaticResourceFinder;
import org.apache.catalina.servlet.ExceptionHandler;

public class UnAuthorizedExceptionHandler implements ExceptionHandler {

    private static final String UNAUTHORIZED_REDIRECT_URI = "http://localhost:8080/401.html";

    @Override
    public HttpResponse handle() {
        return StaticResourceFinder.renderRedirect(UNAUTHORIZED_REDIRECT_URI);
    }
}
