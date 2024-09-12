package org.apache.coyote.http11;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Dispatcher {

    void doDispatch(HttpRequest request, HttpResponse response);
}
