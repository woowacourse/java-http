package org.apache.coyote.http11;

import nextstep.jwp.model.Request;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.vo.FileName;
import nextstep.jwp.vo.FormData;
import nextstep.jwp.vo.LoginResult;

public class PostRequestMangerImpl implements RequestManager {
    private static final String LOGIN = "/login";
    private final Request request;

    public PostRequestMangerImpl(Request request) {
        this.request = request;
    }

    @Override
    public String generateResponse() {
        FileName fileName = request.getFileName();
        FormData requestBody = request.getBody();

        if (fileName.isSame(LOGIN)) {
            return LoginService.signIn(requestBody.get("account"), requestBody.get("password"));
        }

        return RegisterService.signUp(
                requestBody.get("account"),
                requestBody.get("password"),
                requestBody.get("email")
        );
    }
}
