package com.techcourse.presentation;

import com.techcourse.infrastructure.Presentation;
import com.techcourse.presentation.requestparam.UserRequestParam;
import com.techcourse.request.UserRequest;
import com.techcourse.service.LoginService;
import http.HttpMethod;

public class LoginPresentation implements Presentation {

    private static final String URI_PATH = "/login";

    private final LoginService loginService;

    public LoginPresentation(LoginService loginService) {
        this.loginService = loginService;
    }

    public LoginPresentation() {
        this(new LoginService());
    }

    @Override
    public void view(String queryParam) {
        UserRequestParam requestParam = new UserRequestParam(queryParam);
        UserRequest request = requestParam.toObject();
        loginService.findUser(request);
    }

    @Override
    public boolean match(HttpMethod method, String path) {
        return method == HttpMethod.GET && URI_PATH.equals(path);
    }
}
