package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Handler {

    boolean supports(HttpRequest request);

    HttpResponse handle(HttpRequest request);
}
