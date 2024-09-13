package org.apache.coyote.http11;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Servlet {

    void service(HttpRequest req, HttpResponse resp);
}
