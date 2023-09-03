package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class LoginViewController implements Controller {

    @Override
    public Response handle(Request request) {
        Response<Object> response = Response.status(200).build();
        response.responseView("/login.html");
        return response;
    }
}
