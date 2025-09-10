package org.apache.coyote.render;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.util.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserLoginProcessor{

    private static final Logger log = LoggerFactory.getLogger(UserLoginProcessor.class);
    private static final String ACCOUNT = "account";
    private static final String GET_METHOD_REQUEST = "GET";

    public String handle(final String method, final String path, final Map<String,String> queryParams) {
        if (path.startsWith("/login")) {
            handleLogin(method, queryParams);
            return PageRenderer.createStaticFileResponse(path);
        }
        return PageRenderer.createStaticFileResponse("404.html");
    }

    private User handleLogin(final String method, final Map<String, String> queryParams) {
        User user = null;
        if (method.equals(GET_METHOD_REQUEST)) {
            user = InMemoryUserRepository.findByAccount(queryParams.get(ACCOUNT))
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾는 중 에러가 발생했습니다."));
        }
        log.info("user info : {}", user);
        return user;
    }


}
