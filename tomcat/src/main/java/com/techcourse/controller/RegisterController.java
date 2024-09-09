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
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        try {
            Path path = request.getPath();
            return generateStaticResponse(path.getUri() + MimeType.HTML.getExtension(), HttpStatus.OK);
        } catch (NullPointerException e) {
            return new NotFoundController().doGet(request);
        } catch (IOException e) {
            return new InternalServerErrorController().doGet(request);
        }
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        try {
            Path path = request.getPath();
            HttpResponse response = generateStaticResponse(path.getUri() + MimeType.HTML.getExtension(), HttpStatus.FOUND);
            response.setRedirectLocation(REDIRECT_LOCATION);

            loginService.register(request.getBody());

            return response;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return doGet(request);
        } catch (NullPointerException e) {
            return new NotFoundController().doGet(request);
        } catch (IOException e) {
            return new InternalServerErrorController().doGet(request);
        }
    }
}
