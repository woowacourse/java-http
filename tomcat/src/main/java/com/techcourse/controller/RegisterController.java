package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setContentType(ContentType.TEXT_HTML);
        response.setHttpResponseBody(request.getUrlPath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        InMemoryUserRepository.save(new User(
                request.findRequestBodyBy("account"),
                request.findRequestBodyBy("password"),
                request.findRequestBodyBy("email")
        ));
        log.info("savedUser = {}", InMemoryUserRepository.findByAccount(request.findRequestBodyBy("account")));
        response.addHttpResponseHeader(HttpHeader.LOCATION, "/index.html");
        response.setHttpStatus(HttpStatus.FOUND);
        response.setContentType(ContentType.TEXT_HTML);
        response.setHttpResponseBody(request.getUrlPath());
    }
}
