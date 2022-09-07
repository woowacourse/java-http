package org.apache.coyote.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Controller {

    String service(final HttpRequest httpRequest, final HttpResponse httpResponse);

    boolean isRest();
}
