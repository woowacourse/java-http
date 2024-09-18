package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.util.Optional;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.resource.ResourceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    public void service(HttpRequest req, HttpResponse resp) {
        if (req.getMethod().equals(HttpMethod.GET)) {
            File requestFile = ResourceParser.getRequestFile("/register.html");
            resp.setResponse("200 OK", requestFile);
        } else if (req.getMethod().equals(HttpMethod.POST)) {
            try {
                if(!req.hasBody(ACCOUNT) || req.hasBody(PASSWORD) || req.hasBody(EMAIL)) {
                    log.error("요청에 필요한 값이 존재하지 않습니다.");
                    resp.setResponse("404 Not Found", ResourceParser.getRequestFile("/404.html"));
                }
                String account = req.getBodyValue(ACCOUNT);
                String password = req.getBodyValue(PASSWORD);
                String email = req.getBodyValue(EMAIL);

                User user = new User(account, password, email);
                InMemoryUserRepository.save(user);

                resp.setRedirect("302 Found", "/index.html");
                log.info("회원가입 성공 (account: {})", account);
            } catch (IllegalArgumentException e) {
                log.warn("회원가입 실패", e);
                resp.setResponse("400 Bad Request", ResourceParser.getRequestFile("/register.html"));
            }
        } else {
            throw new IllegalArgumentException("%s 요청을 처리할 컨트롤러가 존재하지 않습니다.".formatted(req.getMethod().getValue()));
        }
    }
}
