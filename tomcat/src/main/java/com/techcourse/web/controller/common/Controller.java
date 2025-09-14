package com.techcourse.web.controller.common;

import com.techcourse.web.view.AppResponse;
import com.techcourse.web.request.AppRequest;

public interface Controller {

    AppResponse service(AppRequest request) throws Exception;
}
