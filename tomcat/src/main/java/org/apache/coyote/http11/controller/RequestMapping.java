package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.constants.ContentType;
import org.apache.coyote.http11.request.HttpRequest;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.NotFoundPageController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;

public class RequestMapping {

    public static Controller from(final HttpRequest request) {
        final String uri = request.getUri();

        if (uri.equals("/login")) {
            return new LoginController();
        }
        if (uri.equals("/register")) {
            return new RegisterController();
        }
        if (ContentType.isValidType(uri)) {
            return new StaticResourceController();
        }
        return new NotFoundPageController();
    }
}
