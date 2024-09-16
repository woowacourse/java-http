package com.techcourse.controller;

import com.techcourse.dto.RegisterDto;
import com.techcourse.service.RegisterService;
import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.StatusCode;

public class RegisterController extends AbstractController {

    private final RegisterService registerService = new RegisterService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatus(StatusCode.OK);
        response.setViewUri("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        RegisterDto registerDto = RegisterDto.from(request.getBody());

        registerService.save(registerDto);
        response.setStatus(StatusCode.FOUND);
        response.sendRedirect(request, "/index.html");
    }
}
