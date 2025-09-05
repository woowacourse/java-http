package com.techcourse.web.api;

import com.techcourse.web.HttpRequestHandler;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.techcourse.util.PathParser.parsingQueryString;

public class UserApiProcessor implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(UserApiProcessor.class);
    private static final String ACCOUNT = "account";
    private static final String GET_METHOD_REQUEST = "GET";

    @Override
    public String handle(final String method, final String path) {
        if (path.startsWith("/login")) {
            User user = handleLogin(method, path);
            log.info(user.toString());
            return user.toString();
        }
        return "ERROR";
    }

    private User handleLogin(final String method, final String path) {
        Map<String,String> parsedLoginInfo = parsingQueryString(path);
        User user = null;
        if (method.equals(GET_METHOD_REQUEST)) {
            user = InMemoryUserRepository.findByAccount(parsedLoginInfo.get(ACCOUNT))
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾는 중 에러가 발생했습니다."));
        }
        return user;
    }


}
