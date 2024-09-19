package org.apache.catalina;

import org.apache.coyote.controller.Controller;
import org.apache.http.request.HttpRequest;

public interface HandlerMapping {

    Controller getHandler(final HttpRequest httpRequest);
}
