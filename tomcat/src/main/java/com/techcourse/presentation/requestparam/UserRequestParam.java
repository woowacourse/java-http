package com.techcourse.presentation.requestparam;

import com.techcourse.request.UserRequest;

public class UserRequestParam extends RequestParam<UserRequest> {

    public UserRequestParam(String queryParam) {
        super(queryParam);
    }

    @Override
    public UserRequest toObject() {
        String account = this.getParam("account");
        String password = this.getParam("password");
        return new UserRequest(account, password);
    }
}
