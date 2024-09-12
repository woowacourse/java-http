package org.apache.coyote;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public interface Adapter {

    void service(HttpRequest request, HttpResponse response);
}
