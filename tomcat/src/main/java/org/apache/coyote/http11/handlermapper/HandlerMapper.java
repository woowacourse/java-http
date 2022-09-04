package org.apache.coyote.http11.handlermapper;

import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public interface HandlerMapper {

    Handler mapHandler(HttpRequest httpRequest);
}
