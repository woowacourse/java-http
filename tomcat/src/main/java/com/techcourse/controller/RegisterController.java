package com.techcourse.controller;

import com.techcourse.exception.DuplicateAccountException;
import com.techcourse.service.ApplicationService;
import com.techcourse.web.StaticResourceHandler;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class RegisterController extends AbstractController {

    private final ApplicationService applicationService;
    private final StaticResourceHandler staticResourceHandler;

    public RegisterController(ApplicationService applicationService, StaticResourceHandler staticResourceHandler) {
        this.applicationService = applicationService;
        this.staticResourceHandler = staticResourceHandler;
    }

    @Override
    protected List<String> allowedMethods() {
        return List.of("GET", "POST");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        register(request, request.queryParameters(), response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        register(request, request.formParameters(), response);
    }

    private void register(HttpRequest request, Map<String, String> params, HttpResponse response) throws IOException {
        if (params.get("account") != null && params.get("password") != null && params.get("email") != null) {
            try {
                var user = applicationService.register(params.get("account"), params.get("password"), params.get("email"));
                request.session().setAttribute("user", user);
                response.sendRedirect("/index.html");
                return;
            } catch (DuplicateAccountException e) {
                response.copyFrom(staticResourceHandler.createResponse("/register.html"));
                return;
            }
        }
        response.copyFrom(staticResourceHandler.createResponse("/register.html"));
    }
}
