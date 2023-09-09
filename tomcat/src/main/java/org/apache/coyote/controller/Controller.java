package org.apache.coyote.controller;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public interface Controller {

    void service(Request request, Response response);
}
