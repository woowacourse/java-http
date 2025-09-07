package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        Map<String, String> queryParams = httpRequest.getQueryParams();

        // account, password 쿼리 파라미터가 둘 다 없는 경우 login.html 반환
        if (!queryParams.containsKey("account")
                && !queryParams.containsKey("password")) {
            String body = ResourceUtil.readStaticResource("/login.html", this.getClass());

            return ResponseEntity.ok(body, "text/html;charset=utf-8");
        }

        // account나 password 중 하나만 없는 경우, 파라미터 누락 처리
        if (!queryParams.containsKey("account")
                || !queryParams.containsKey("password")) {

            return ResponseEntity.badRequest("account or password is missing.");
        }

        String account = queryParams.get("account");
        String password = queryParams.get("password");
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
        if (foundUser.isEmpty()) {
            return ResponseEntity.unauthorized("login failed.");
        }

        User user = foundUser.get();
        if (user.checkPassword(password)) {
            log.debug("{}", user);

            return ResponseEntity.ok("login success!");
        }

        return ResponseEntity.unauthorized("login failed.");
    }

    @Override
    public String getPath() {
        return "/login";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
