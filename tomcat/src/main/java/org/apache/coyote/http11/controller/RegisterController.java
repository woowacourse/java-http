package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httprequest.QueryParameter;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;
import org.apache.coyote.http11.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    public HttpResponse process(HttpRequest request) {
        if (request.isGetMethod()) {
            String location = request.getUri() + Constants.EXTENSION_OF_HTML;
            return HttpResponse.of(location, HttpStatusCode.OK);
        }
        if (request.isPostMethod()) {
            QueryParameter queryParameter = new QueryParameter(request.getBody());
            User user = findUser(queryParameter);

            InMemoryUserRepository.save(user);
            log.info("회원가입 완료! 아이디: {}", user.getAccount());

            return HttpResponse.of(Constants.DEFAULT_URI, HttpStatusCode.OK);
        }
        return null;
    }

    private User findUser(QueryParameter queryParameter) {
        String account = queryParameter.get(Constants.PARAMETER_KEY_OF_ACCOUNT);
        String password = queryParameter.get(Constants.PARAMETER_KEY_OF_PASSWORD);
        String email = queryParameter.get(Constants.PARAMETER_KEY_OF_EMAIL);

        return new User(account, password, email);
    }

}
