package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    boolean supports(final HttpRequest httpRequest);

    HttpResponse handle(final HttpRequest httpRequest);
}
