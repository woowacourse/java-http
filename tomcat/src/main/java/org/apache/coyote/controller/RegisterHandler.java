package org.apache.coyote.controller;

import static org.apache.coyote.http.StatusCode.BAD_REQUEST;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http.Header;
import org.apache.coyote.http.StatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class RegisterHandler implements Handler {

    private static final String ACCOUNT_FIELD = "account";
    private static final String PASSWORD_FIELD = "password";
    private static final String EMAIL_FIELD = "email";

    @Override
    public HttpResponse handle(HttpRequest request) {
        String account = request.getParameter(ACCOUNT_FIELD);
        String password = request.getParameter(PASSWORD_FIELD);
        String email = request.getParameter(EMAIL_FIELD);

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return new HttpResponse(BAD_REQUEST, Map.of(), null);
        }
        InMemoryUserRepository.save(new User(account, password, email));
        return new HttpResponse(StatusCode.FOUND,
                                Map.of(Header.LOCATION.value(), "/index.html"),
                                null);
    }
}
