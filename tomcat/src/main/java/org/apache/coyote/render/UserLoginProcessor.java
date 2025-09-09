package org.apache.coyote.render;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserLoginProcessor{

    private static final Logger log = LoggerFactory.getLogger(UserLoginProcessor.class);
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String GET_METHOD_REQUEST = "GET";

    public String handle(final String method, final String path, final Map<String,String> queryParams) {
        try {
            if (path.equals("/login")) {
                handleLogin(method, queryParams);
            }
        } catch (IllegalArgumentException e) {
            return PageRenderer.createStaticFileResponse(HttpStatus.UNAUTHORIZED.getStatusCode(), "/401.html");

        }
        return PageRenderer.createStaticFileResponse(HttpStatus.FORBIDDEN.getStatusCode(), "/index.html");
    }

    private User handleLogin(final String method, final Map<String, String> queryParams) {
        User user = null;
        if (method.equals(GET_METHOD_REQUEST)) {
            user = InMemoryUserRepository.findByAccount(queryParams.get(ACCOUNT))
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));
            checkUserPassword(user,queryParams.get(PASSWORD));
        }
        log.info("user info : {}", user);
        return user;
    }

    private void checkUserPassword(final User user, final String inputPassword) {
        if (!user.checkPassword(inputPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }


}
