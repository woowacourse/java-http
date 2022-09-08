package org.apache.coyote.http11.handlermapper;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public interface HandlerMapper {

    Controller mapHandler(HttpRequest httpRequest);
}
