package org.apache.coyote.adapter;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Adapter {
    void service(HttpRequest request, HttpResponse response);
}
