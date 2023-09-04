package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.SessionManager;

public class LoginViewController implements Controller {

    private static final String LOCATION_HEADER = "Location";

    @Override
    public HttpResponse<Object> handle(HttpRequest httpRequest) {
        if (SessionManager.loggedIn(httpRequest)) {
            return HttpResponse.status(302)
                .addHeader(LOCATION_HEADER, "/index.html")
                .build();
        }
        HttpResponse<Object> httpResponse = HttpResponse.status(200).build();
        httpResponse.responseView("/login.html");
        return httpResponse;
    }
}
