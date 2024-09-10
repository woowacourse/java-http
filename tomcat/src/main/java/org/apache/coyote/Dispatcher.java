package org.apache.coyote;

import org.apache.coyote.http11.protocol.request.HttpRequest;
import org.apache.coyote.http11.protocol.response.HttpResponse;

public interface Dispatcher {
    
    void dispatch(HttpRequest request, HttpResponse response);
}
