package com.techcourse.web.controller.common;

import com.techcourse.web.request.AppRequest;
import com.techcourse.web.view.AppResponse;
import com.techcourse.web.view.StandardResponse;

public abstract class AbstractController implements Controller {

    @Override
    public AppResponse service(final AppRequest request) {
        return switch (request.getMethod()) {
            case GET -> doGet(request);
            case POST -> doPost(request);
            default -> StandardResponse.methodNotAllowed();
        };
    }

    protected AppResponse doPost(final AppRequest request) {
        return StandardResponse.methodNotAllowed();
    }

    protected AppResponse doGet(final AppRequest request) {
        return StandardResponse.methodNotAllowed();
    }
}
