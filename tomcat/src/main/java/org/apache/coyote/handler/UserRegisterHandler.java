package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.render.HttpStatus;
import org.apache.coyote.render.PageRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserRegisterHandler implements RequestHandler{
    private static final Logger log = LoggerFactory.getLogger(UserRegisterHandler.class);
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String POST_METHOD_REQUEST = "POST";
    private static final String GET_METHOD_REQUEST = "GET";

    @Override
    public String handle(final String method, final String path, final Map<String, String> queryParams, HttpCookie cookie) {
        if (method.equals(GET_METHOD_REQUEST)) {
            return handleRegisterGet();
        }
        if (method.equals(POST_METHOD_REQUEST)) {
            return handleRegisterPost(method, queryParams);
        }
        throw new IllegalArgumentException("회원가입중 문제가 발생했습니다.");
    }

    private String handleRegisterGet() {
        return PageRenderer.createStaticFileResponse(HttpStatus.OK.getStatusCode(), "/register.html");
    }

    private String handleRegisterPost(String method, Map<String, String> queryParams) {
        try {
            if (method.equals(POST_METHOD_REQUEST)) {
                validateAccountDuplicated(queryParams);

                User beforeSaveUser = new User(
                        queryParams.get(ACCOUNT),
                        queryParams.get(PASSWORD),
                        queryParams.get(EMAIL)
                );
                InMemoryUserRepository.save(beforeSaveUser);
            }
        } catch (IllegalArgumentException e) {
            return PageRenderer.createStaticFileResponse(HttpStatus.UNAUTHORIZED.getStatusCode(), "/401.html");
        }
        return PageRenderer.sendRedirect(HttpStatus.FOUND.getStatusCode(), "/");
    }


    private void validateAccountDuplicated(final Map<String, String> queryParams) {
        if (InMemoryUserRepository.findByAccount(queryParams.get(ACCOUNT)).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
    }
}
