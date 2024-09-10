package org.apache.coyote.http11.component.exceptionhandler;

import org.apache.coyote.http11.component.response.HttpResponse;

public interface ExceptionHandler {

    HttpResponse handle();
}
