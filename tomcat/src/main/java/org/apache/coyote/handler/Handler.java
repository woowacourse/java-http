package org.apache.coyote.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Handler {

    HttpResponse handle(HttpRequest httpRequest);
}
