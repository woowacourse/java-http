package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryParameter;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    public HttpResponse process(HttpRequest request) {
        if (request.isGetMethod()) {
            return new HttpResponse(request.getPath() + Constants.EXTENSION_OF_HTML, HttpStatusCode.OK);
        }
        if (request.isPostMethod()) {
            registerUser(request);
            return new HttpResponse(Constants.DEFAULT_URI, HttpStatusCode.FOUND);
        }
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    private void registerUser(HttpRequest request) {
        User user = generateUser(new QueryParameter(request.getBody()));
        InMemoryUserRepository.save(user);

        log.info("회원가입 완료! 아이디: {}", user.getAccount());
    }

    private User generateUser(QueryParameter queryParameter) {
        String account = queryParameter.get(Constants.PARAMETER_KEY_OF_ACCOUNT);
        String password = queryParameter.get(Constants.PARAMETER_KEY_OF_PASSWORD);
        String email = queryParameter.get(Constants.PARAMETER_KEY_OF_EMAIL);

        return new User(account, password, email);
    }
}
