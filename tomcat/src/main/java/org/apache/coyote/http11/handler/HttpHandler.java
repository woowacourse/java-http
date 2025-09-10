package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface HttpHandler {

    HttpResponse handle(HttpRequest request) throws Exception;
}
