package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.annotaion.GetMapping;
import org.apache.coyote.annotaion.PostMapping;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.RequestBodyParser;
import org.apache.coyote.util.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public void loginPage(HttpRequest request, HttpResponse response) {
        ViewResolver.resolveView("login.html", response);
    }

    @PostMapping("/login")
    public void login(HttpRequest request, HttpResponse response) {
        Map<String, String> formData = RequestBodyParser.parseFormData(request.getBody());
        String account = formData.get("account");
        String password = formData.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseGet(() -> {
                    log.debug("{} - 존재하지 않는 회원의 로그인 요청", account);
                    response.updateHttpStatus(HttpStatus.UNAUTHORIZED);
                    throw new IllegalArgumentException("존재하지 않는 회원 입니다.");
                });
        validatePassword(request, response, user, password);
        log.info("{} - 회원 로그인 성공", user);
        response.sendRedirect("/index.html");
    }

    private void validatePassword(HttpRequest request, HttpResponse response, User user, String password) {
        if (!user.checkPassword(password)) {
            log.debug("회원과 일치하지 않는 비밀번호 - 회원 정보 : {}, 입력한 비밀번호 {}", user, password);
            response.updateHttpStatus(HttpStatus.UNAUTHORIZED);
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
    }
}
