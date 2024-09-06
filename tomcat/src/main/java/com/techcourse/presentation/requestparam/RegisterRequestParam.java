package com.techcourse.presentation.requestparam;

import com.techcourse.request.RegisterRequest;

public class RegisterRequestParam extends RequestParam<RegisterRequest> {

    public RegisterRequestParam(String queryParam) {
        super(queryParam);
    }

    @Override
    public RegisterRequest toObject() {
        String account = this.getParam("account");
        String email = this.getParam("email");
        String password = this.getParam("password");
        return new RegisterRequest(account, email, password);
    }
}
