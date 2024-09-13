package com.techcourse.controller;

import static com.techcourse.controller.PagePath.INDEX_PAGE;
import static com.techcourse.controller.PagePath.NOT_FOUND_PAGE;

import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.was.controller.AbstractController;
import org.was.controller.ResponseResult;

public class DashboardController extends AbstractController {

    @Override
    protected ResponseResult doPost(HttpRequest request) {
        return ResponseResult
                .status(HttpStatusCode.NOT_FOUND)
                .body(NOT_FOUND_PAGE.getPath());
    }

    @Override
    protected ResponseResult doGet(HttpRequest request) {
        return ResponseResult
                .status(HttpStatusCode.OK)
                .path(INDEX_PAGE.getPath());
    }
}
