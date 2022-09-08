package org.apache.catalina;

import org.apache.coyote.http11.HttpResponse;

public interface ExceptionHandler {

    HttpResponse handle(Exception e);
}
