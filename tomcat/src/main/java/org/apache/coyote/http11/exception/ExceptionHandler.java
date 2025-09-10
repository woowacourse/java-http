package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface ExceptionHandler {

    boolean support(Exception e);

    void handle(HttpRequest request, HttpResponse response, Exception e);
}
