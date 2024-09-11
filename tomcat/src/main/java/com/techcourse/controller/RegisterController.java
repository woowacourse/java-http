package com.techcourse.controller;

import com.techcourse.dto.RegisterRequestDto;
import com.techcourse.service.LoginService;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.HttpMessageGenerator;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.coyote.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RegisterController extends AbstractController {

    private static final String REGISTER_LOCATION = "/register.html";
    private static final String REDIRECT_LOCATION = "/index.html";

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final LoginService loginService = new LoginService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        try {
            HttpMessageGenerator.generateStaticResponse(REGISTER_LOCATION, HttpStatus.OK, response);
        } catch (NullPointerException e) {
            new NotFoundController().doGet(request, response);
        } catch (IOException e) {
            new InternalServerErrorController().doGet(request, response);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        try {
            HttpMessageGenerator.generateStaticResponse(REGISTER_LOCATION, HttpStatus.FOUND, response);
            response.setRedirectLocation(REDIRECT_LOCATION);

            RegisterRequestDto registerRequestDto = RegisterRequestDto.of(StringUtils.separateKeyValue(request.getBody()));
            loginService.register(registerRequestDto);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            doGet(request, response);
        } catch (NullPointerException e) {
            new NotFoundController().doGet(request, response);
        } catch (IOException e) {
            new InternalServerErrorController().doGet(request, response);
        }
    }
}
