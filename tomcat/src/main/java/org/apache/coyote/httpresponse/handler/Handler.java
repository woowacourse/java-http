package org.apache.coyote.httpresponse.handler;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;

public interface Handler {

    HttpResponse handle(final HttpRequest request);
}
