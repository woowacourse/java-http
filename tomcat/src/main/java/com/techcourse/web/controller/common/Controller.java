package com.techcourse.web.controller.common;

import com.techcourse.web.request.AppRequest;
import com.techcourse.web.view.AppResponse;

public interface Controller {

    AppResponse service(AppRequest request) throws Exception;
}
