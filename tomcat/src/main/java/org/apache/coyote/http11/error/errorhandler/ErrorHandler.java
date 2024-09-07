package org.apache.coyote.http11.error.errorhandler;

import org.apache.coyote.http11.response.HttpResponse;

public interface ErrorHandler {
    HttpResponse handleError();
}
