package com.techcourse.web.controller;

import static common.HttpConstants.KEY_VALUE_SEPARATOR;
import static common.HttpConstants.LOCATION_HEADER_NAME;
import static common.HttpConstants.SET_COOKIE_HEADER_NAME;
import static common.session.SessionManager.JSESSIONID;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.web.controller.common.AbstractController;
import com.techcourse.web.controller.common.StaticFileResolver;
import com.techcourse.web.request.AppRequest;
import com.techcourse.web.view.AppResponse;
import com.techcourse.web.view.StandardResponse;
import common.session.Session;
import common.session.SessionManager;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginController extends AbstractController {

    @Override
    protected AppResponse doPost(final AppRequest request) {
        try {
            final User user = processLogin(request);
            final Session session = new Session();
            session.setAttribute(session.getId(), user);
            SessionManager.getInstance().add(session);
            final Map<String, String> header = new HashMap<>();
            header.put(SET_COOKIE_HEADER_NAME, JSESSIONID + KEY_VALUE_SEPARATOR + session.getId());
            header.put(LOCATION_HEADER_NAME, "/index.html");
            log.debug("로그인 성공: {}", user.getAccount());
            return StandardResponse.found(header);
        } catch (final Exception e) {
            log.debug("로그인 실패: {}", e.getMessage());
            return StandardResponse.found("/401.html");
        }
    }

    @Override
    protected AppResponse doGet(final AppRequest request) {
        // 로그인 확인
        final boolean alreadyLogin = SessionManager.getInstance().isValidSession(request.getSession().getId());
        if (alreadyLogin) {
            return StandardResponse.found("/index.html");
        }

        return StaticFileResolver.resolve(request);
    }

    private User processLogin(final AppRequest request) {
        final String account = request.getBodyParam("account");
        final String password = request.getBodyParam("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchElementException("계정을 찾을 수 없습니다."));

        user.checkPassword(password);
        return user;
    }
}
