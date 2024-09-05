package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public boolean canHandle(String url) {
        return url.contains("login");
    }

    @Override
    public Map<String, String> handle(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();
        if (requestLine.isQueryStringRequest()) {
            return checkLogin(requestLine);
        }

        return resolveResponse(200, "/login.html");
    }

    private Map<String, String> checkLogin(RequestLine requestLine) {
        Map<String, String> parameters = requestLine.getParameters();
        String account = parameters.get("account");
        String password = parameters.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new SecurityException("잘못된 유저 정보 입니다."));

        if (!user.checkPassword(password)) {
            throw new SecurityException("잘못된 유저 정보 입니다.");
        }

        log.info("user : {}", user);
        return resolveResponse(302, "/index.html");
    }

    private Map<String, String> resolveResponse(int statusCode, String viewUrl){
        Map<String, String> map = new HashMap<>();
        map.put("statusCode", String.valueOf(statusCode));
        map.put("url", viewUrl);
        return map;
    }
}
