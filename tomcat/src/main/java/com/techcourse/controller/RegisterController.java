package com.techcourse.controller;

import com.techcourse.service.LoginService;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.Path;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RegisterController extends AbstractController {

    private static final String REDIRECT_LOCATION = "/index.html";
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final LoginService loginService = new LoginService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        try {
            Path path = request.getPath();
            generateStaticResponse(path.getUri() + MimeType.HTML.getExtension(), HttpStatus.OK, response);
        } catch (NullPointerException e) {
            new NotFoundController().doGet(request, response);
        } catch (IOException e) {
            new InternalServerErrorController().doGet(request, response);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        try {
            Path path = request.getPath();
            generateStaticResponse(path.getUri() + MimeType.HTML.getExtension(), HttpStatus.FOUND, response);
            response.setRedirectLocation(REDIRECT_LOCATION);

            loginService.register(request.getBody());
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
