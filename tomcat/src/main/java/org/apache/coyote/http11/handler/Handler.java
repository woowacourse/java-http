package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Handler {

    boolean supports(HttpRequest request);

    HttpResponse handle(HttpRequest request);
}
