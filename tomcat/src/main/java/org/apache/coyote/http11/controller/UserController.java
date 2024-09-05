package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.model.UserInfo;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public boolean canHandle(String url) {
        return url.contains("register");
    }

    @Override
    public Map<String, String> handle(HttpRequest httpRequest) {
        if (httpRequest.isMethod(HttpMethod.POST)) {
            UserInfo userInfo = UserInfo.read(httpRequest.getRequestBody());
            User user = new User(userInfo);
            InMemoryUserRepository.save(user);
            log.info("새로운 user: {}", user);
            return resolveResponse(302, "/index.html");
        }

        return resolveResponse(200, "/register.html");
    }

    private Map<String, String> resolveResponse(int statusCode, String viewUrl) {
        Map<String, String> map = new HashMap<>();
        map.put("statusCode", String.valueOf(statusCode));
        map.put("url", viewUrl);
        return map;
    }
}
