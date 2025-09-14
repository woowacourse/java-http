package com.techcourse.web.controller;

import com.techcourse.web.controller.common.AbstractController;
import com.techcourse.web.view.AppResponse;
import com.techcourse.web.view.StandardResponse;
import com.techcourse.web.request.AppRequest;
import common.ContentType;

public class HomeController extends AbstractController {

    @Override
    protected AppResponse doGet(final AppRequest request) {
        return StandardResponse.ok(ContentType.DEFAULT_RESPONSE_CONTENT_TYPE, "Hello world!");
    }
}
