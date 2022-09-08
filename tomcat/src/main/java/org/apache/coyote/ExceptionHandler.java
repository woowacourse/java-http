package org.apache.coyote;

import org.apache.coyote.http11.message.response.HttpResponse;

public interface ExceptionHandler {

    HttpResponse handle(final Exception e);
}
