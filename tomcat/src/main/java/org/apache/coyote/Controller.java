package org.apache.coyote;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponseComposer;

public interface Controller {

    void service(HttpRequest httpRequest, HttpResponseComposer httpResponseComposer);
}
