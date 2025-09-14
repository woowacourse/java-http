package com.techcourse.web.controller;

import com.techcourse.web.controller.common.AbstractController;
import com.techcourse.web.request.AppRequest;
import com.techcourse.web.view.AppResponse;
import com.techcourse.web.view.StandardResponse;
import common.ContentType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public class HomeController extends AbstractController {

    private static final HomeController INSTANCE = new HomeController();

    public static HomeController getInstance() {
        return INSTANCE;
    }

    @Override
    protected AppResponse doGet(final AppRequest request) {
        return StandardResponse.ok(ContentType.DEFAULT_RESPONSE_CONTENT_TYPE, "Hello world!");
    }
}
