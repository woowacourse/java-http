package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public interface Handler {

    boolean canHandle(HttpRequest httpRequest);

    Object getResponse(HttpRequest httpRequest);

}
