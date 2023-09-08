package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpQueryParser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Map;

import static org.apache.coyote.http11.controller.URIPath.LOGIN_URI;
import static org.apache.coyote.http11.controller.URIPath.REGISTER_URI;
import static org.apache.coyote.http11.types.HeaderType.LOCATION;
import static org.apache.coyote.http11.types.HttpStatus.FOUND;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> queries = HttpQueryParser.parse(request.getPath());

        String account = queries.get("account");
        String password = queries.get("password");
        String email = queries.get("email");

        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            response.addHeader(LOCATION, REGISTER_URI);
            response.setHttpStatus(FOUND);
        }

        User registUser = new User(account, password, email);
        InMemoryUserRepository.save(registUser);

        if (log.isInfoEnabled()) {
            log.info(String.format("%s %s", "회원가입 성공!", registUser));
        }

        response.addHeader(LOCATION, LOGIN_URI);
        response.setHttpStatus(FOUND);
    }
}
