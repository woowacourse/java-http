package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.io.IOException;
import org.apache.catalina.AbstractController;
import org.apache.catalina.resource.ResourceReader;
import org.apache.coyote.http11.data.ContentType;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpRequestParameter;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.HttpStatusCode;
import org.apache.coyote.http11.data.MediaType;

public class RegisterController extends AbstractController {
    private static final RegisterController INSTANCE = new RegisterController();

    private final ResourceReader resourceReader = ResourceReader.getInstance();

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String redirectUrl = "/index.html";
        HttpRequestParameter requestParameter = request.getHttpRequestParameter();
        try {
            UserService.createUser(requestParameter);
        } catch (IllegalArgumentException e) {
            redirectUrl = "/400.html";
        }
        response.setHttpStatusCode(HttpStatusCode.FOUND)
                .setRedirectUrl(redirectUrl);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String responseBody = resourceReader.loadResourceAsString("register.html");
        response.setContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                .setHttpStatusCode(HttpStatusCode.OK)
                .setResponseBody(responseBody);
    }
}
