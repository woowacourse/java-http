package com.techcourse.controller;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.constants.ContentType;
import org.apache.coyote.http11.constants.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.constants.HttpStatus;
import org.apache.coyote.http11.util.StaticResourceReader;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final String[] loginRequest = request.getBody().split("&");
        final String account = loginRequest[0].split("=")[1];
        final String email = loginRequest[1].split("=")[1];
        final String password = loginRequest[2].split("=")[1];

        final User user = new User(account, email, password);
        InMemoryUserRepository.save(user);

        response.setHttpStatus(HttpStatus.FOUND);
        response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.getContentTypeUtf8());
        response.addHeader(HttpHeader.LOCATION, "/index.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String body = StaticResourceReader.read("/register.html");
        response.setHttpStatus(HttpStatus.OK);
        response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.getContentTypeUtf8());
        response.setBody(body);
    }
}
