package org.apache.catalina;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    boolean support(HttpRequest request);

    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;
}
