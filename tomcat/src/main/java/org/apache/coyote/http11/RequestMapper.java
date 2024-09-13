package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticPageController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestMapper {

    private static final RequestMapper instance = new RequestMapper();

    private final LoginController loginController;

    private final RegisterController registerController;

    private final StaticPageController staticPageController;

    private RequestMapper() {
        this.loginController = LoginController.getInstance();
        this.registerController = RegisterController.getInstance();
        this.staticPageController = StaticPageController.getInstance();
    }

    public Controller mapRequest(HttpRequest request) {
        if (request.getHttpRequestPath().contains("/login")) {
            return loginController;
        }
        if (request.getHttpRequestPath().contains("/register")) {
            return registerController;
        }
        return staticPageController;
    }

    public static RequestMapper getInstance() {
        return instance;
    }
}
