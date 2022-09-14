package org.apache.coyote.controller;

import org.apache.coyote.http11.httpmessage.request.Request;
import org.apache.coyote.http11.httpmessage.response.Response;

public interface Controller {

    void service(Request request, Response response) throws Exception;
}
