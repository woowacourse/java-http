package org.apache.coyote.controller;

import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.response.MyHttpResponse;

public interface Controller {

    void service(MyHttpRequest request, MyHttpResponse response) throws Exception;
}
