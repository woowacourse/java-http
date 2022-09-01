package org.apache.coyote.http11;

public interface HttpRequestProcessor {

    HttpResponse process(HttpRequest request);
}
