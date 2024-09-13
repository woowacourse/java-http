package org.apache.coyote.container;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface ServletContainer {

    void invoke(HttpRequest httpRequest, HttpResponse httpResponse);
}
