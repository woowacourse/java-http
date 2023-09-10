package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;

public interface Controller {
    void service(Request request, Response response) throws Exception;
}
