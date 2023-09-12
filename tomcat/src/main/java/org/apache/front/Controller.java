package org.apache.front;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Controller {

    void service(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
