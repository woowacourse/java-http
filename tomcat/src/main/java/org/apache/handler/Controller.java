package org.apache.handler;

import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;

public interface Controller {

    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;
}
