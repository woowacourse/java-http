package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import com.techcourse.param.RegisterParam;
import com.techcourse.service.RegisterService;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.HttpStatus;

public class RegisterController extends AbstractController {

    private static final String REGISTER_PATH = "/register";
    private static final String MAIN_PATH = "/index.html";

    private final RegisterService registerService;

    public RegisterController() {
        this.registerService = new RegisterService();
    }

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return requestLine.isSamePath(REGISTER_PATH);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            RegisterParam registerParam = new RegisterParam(httpRequest);
            registerService.addRegister(
                    registerParam.getAccount(),
                    registerParam.getPassword(),
                    registerParam.getEmail()
            );

            httpResponse.sendRedirect(MAIN_PATH);
        } catch (IllegalArgumentException e) {
            httpResponse.sendRedirect(REGISTER_PATH);
        }
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.sendStaticResourceResponse(httpRequest, HttpStatus.OK);
    }
}
