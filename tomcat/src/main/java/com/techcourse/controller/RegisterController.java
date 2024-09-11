package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.MissingRequestBodyException;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.was.Controller.AbstractController;
import org.was.Controller.ResponseResult;

public class RegisterController extends AbstractController {

    @Override
    protected ResponseResult doGet(HttpRequest request) {
        return ResponseResult
                .status(HttpStatusCode.OK)
                .path("/register.html");
    }

    @Override
    protected ResponseResult doPost(HttpRequest request) {
        try {
            User user = extractUserByFormData(request);
            InMemoryUserRepository.save(user);
            return ResponseResult
                    .status(HttpStatusCode.FOUND)
                    .header(HttpHeaders.LOCATION.getName(), "/index.html")
                    .build();

        } catch (MissingRequestBodyException e) {
            return ResponseResult
                    .status(HttpStatusCode.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    private User extractUserByFormData(HttpRequest request) {
        if (!request.hasBodyData()) {
            throw new MissingRequestBodyException();
        }

        Map<String, String> requestFormData = request.getFormData();
        String account = requestFormData.get("account");
        String password = requestFormData.get("password");
        String email = requestFormData.get("email");

        return new User(account, password, email);
    }
}
