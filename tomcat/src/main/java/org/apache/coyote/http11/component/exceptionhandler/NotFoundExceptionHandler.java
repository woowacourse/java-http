package org.apache.coyote.http11.component.exceptionhandler;

import org.apache.coyote.http11.component.resource.StaticResourceFinder;
import org.apache.coyote.http11.component.response.HttpResponse;

public class NotFoundExceptionHandler implements ExceptionHandler {

    private static final String NOT_FOUND_HTML = "404.html";

    @Override
    public HttpResponse handle() {
        return StaticResourceFinder.render(NOT_FOUND_HTML);
    }
}
