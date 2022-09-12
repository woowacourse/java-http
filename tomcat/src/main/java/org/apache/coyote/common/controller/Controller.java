package org.apache.coyote.common.controller;

import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;

public interface Controller {

    void service(Request request, Response response) throws Exception;
}
