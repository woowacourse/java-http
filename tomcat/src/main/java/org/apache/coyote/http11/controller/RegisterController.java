package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return respondWithStaticResource(request, "/register.html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getBodyParamValue("account");
        String email = request.getBodyParamValue("email");
        String password = request.getBodyParamValue("password");

        if (account == null || account.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {
            return HttpResponse.badRequest(request.getVersion());
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            byte[] body = "<p>이미 존재하는 account 입니다.</p>".getBytes(StandardCharsets.UTF_8);
            return HttpResponse.conflict(request.getVersion(), body);
        }

        InMemoryUserRepository.save(new User(account, password, email));
        log.info("회원가입 완료: {}", account);
        return HttpResponse.redirect(request.getVersion(), "/index.html");
    }
}
