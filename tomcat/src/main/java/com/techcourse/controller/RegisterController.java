package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.session.SessionManager;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.startline.HttpStatus;

@RequestMapping("/register")
public class RegisterController extends RestController {

    @Override
    boolean doGet(HttpRequest request, HttpResponse response) {
        return false;
    }

    @Override
    boolean doPost(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getFromBody("account"),
                request.getFromBody("password"),
                request.getFromBody("email")
        );
        InMemoryUserRepository.save(user);

        String jSessionId = SessionManager.addUser(user);
        response.addSessionToCookies(jSessionId);

        return redirectToIndex(response);
    }

    private boolean redirectToIndex(HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.addHeader(HttpHeader.LOCATION, "/index");
        return response.isValid();
    }
}
