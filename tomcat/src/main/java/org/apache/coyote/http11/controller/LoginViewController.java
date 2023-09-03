package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.session.SessionManager;

public class LoginViewController implements Controller {

    private static final String LOCATION_HEADER = "Location";

    @Override
    public Response<Object> handle(Request request) {
        if (SessionManager.loggedIn(request)) {
            return Response.status(302)
                .addHeader(LOCATION_HEADER, "/index.html")
                .build();
        }
        Response<Object> response = Response.status(200).build();
        response.responseView("/login.html");
        return response;
    }
}
