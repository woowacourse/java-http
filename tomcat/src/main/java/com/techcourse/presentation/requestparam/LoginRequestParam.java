package com.techcourse.presentation.requestparam;

import com.techcourse.request.UserRequest;

public class LoginRequestParam extends RequestParam<UserRequest> {

    public LoginRequestParam(String queryParam) {
        super(queryParam);
    }

    @Override
    public UserRequest toObject() {
        String account = this.getParam("account");
        String password = this.getParam("password");
        return new UserRequest(account, password);
    }
}
