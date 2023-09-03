package org.apache.coyote;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public interface Controller {

    void service(HttpRequest httpRequest, HttpResponse httpResponse);
}
