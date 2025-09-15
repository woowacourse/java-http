package com.techcourse.web.controller;

import com.techcourse.model.User;
import com.techcourse.service.UserService;
import com.techcourse.web.controller.common.AbstractController;
import com.techcourse.web.controller.common.StaticFileResolver;
import com.techcourse.web.request.AppRequest;
import com.techcourse.web.view.AppResponse;
import com.techcourse.web.view.StandardResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.NONE)
@Slf4j
public class RegisterController extends AbstractController {

    private static final RegisterController INSTANCE = new RegisterController();
    private final UserService userService = UserService.getInstance();

    public static RegisterController getInstance() {
        return INSTANCE;
    }

    @Override
    protected AppResponse doPost(final AppRequest request) {
        try {
            final String account = request.getBodyParam("account");
            final String password = request.getBodyParam("password");
            final String email = request.getBodyParam("email");

            final User user = userService.registerUser(account, password, email);
            log.debug("회원 가입 성공: {}", user);
            return StandardResponse.found("/index.html");
        } catch (final Exception e) {
            log.debug("회원 가입 실패: {}", e.getMessage());
            return StandardResponse.found("/401.html");
        }
    }

    @Override
    protected AppResponse doGet(final AppRequest request) {
        return StaticFileResolver.resolve(request);
    }
}
