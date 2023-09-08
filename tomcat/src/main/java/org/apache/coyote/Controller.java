package org.apache.coyote;

import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
