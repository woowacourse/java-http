package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpResponse;

public interface HttpRequestProcessor {

    HttpResponse process(HttpRequest request);
}
