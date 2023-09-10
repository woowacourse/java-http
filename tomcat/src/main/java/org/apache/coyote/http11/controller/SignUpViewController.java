package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.session.SessionManager;

public class SignUpViewController implements Controller {

    private static final String LOCATION_HEADER = "Location";

    @Override
    public ResponseEntity<Object> handle(HttpRequest httpRequest) {
        if (SessionManager.loggedIn(httpRequest)) {
            return ResponseEntity.status(302)
                .addHeader(LOCATION_HEADER, "/index.html")
                .build();
        }
        ResponseEntity<Object> responseEntity = ResponseEntity.status(200).build();
        responseEntity.responseView("/register.html");
        return responseEntity;
    }
}
