package com.techcourse.web.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.web.controller.common.AbstractController;
import com.techcourse.web.controller.common.StaticFileResolver;
import com.techcourse.web.request.AppRequest;
import com.techcourse.web.view.AppResponse;
import com.techcourse.web.view.StandardResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegisterController extends AbstractController {

    @Override
    protected AppResponse doPost(final AppRequest request) {
        try {
            final User user = createUser(request);
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

    private User createUser(final AppRequest request) {
        final String account = request.getBodyParam("account");
        final String password = request.getBodyParam("password");
        final String email = request.getBodyParam("email");
        return InMemoryUserRepository.save(
                User.withoutId(account, password, email));
    }
}
