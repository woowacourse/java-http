package com.techcourse.presentation.requestparam;

import com.techcourse.presentation.RequestParam;
import com.techcourse.request.UserRequest;

public class UserRequestParam extends RequestParam<UserRequest> {

    public UserRequestParam(String queryString) {
        super(queryString);
    }

    @Override
    public UserRequest toObject() {
        String account = this.getParam("account");
        String password = this.getParam("password");
        return new UserRequest(account, password);
    }
}
