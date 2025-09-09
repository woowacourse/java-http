package org.apache.coyote.render;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserRegisterHandler implements RequestHandler{
    private static final Logger log = LoggerFactory.getLogger(UserRegisterHandler.class);
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String POST_METHOD_REQUEST = "post";

    @Override
    public String handle(final String method, final String path, final Map<String, String> queryParams) {
        try {
            if (path.equals("/register")) {
                handleRegister(method, queryParams);
            }
        } catch (IllegalArgumentException e) {
            return PageRenderer.createStaticFileResponse(HttpStatus.UNAUTHORIZED.getStatusCode(), "/401.html");

        }
        return PageRenderer.sendRedirect(HttpStatus.FOUND.getStatusCode(), "/");
    }

    private void handleRegister(final String method, final Map<String, String> queryParams) {
        if (method.equals(POST_METHOD_REQUEST)) {
            validateAccountDuplicated(queryParams);

            User beforeSaveUser = new User(
                    queryParams.get(ACCOUNT),
                    queryParams.get(PASSWORD),
                    queryParams.get(EMAIL)
            );

            InMemoryUserRepository.save(beforeSaveUser);
        }
    }

    private void validateAccountDuplicated(final Map<String, String> queryParams) {
        if (InMemoryUserRepository.findByAccount(queryParams.get(ACCOUNT)).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
    }
}
