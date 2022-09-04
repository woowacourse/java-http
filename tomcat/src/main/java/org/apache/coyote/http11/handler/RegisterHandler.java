package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.header.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.http.HttpVersion.HTTP11;
import static org.apache.coyote.http11.http.response.HttpStatus.REDIRECT;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.DuplicateUserException;
import nextstep.jwp.model.User;
import org.apache.catalina.utils.Parser;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class RegisterHandler extends ResourceHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String body = httpRequest.getBody();

        final Map<String, String> queryParams = Parser.parseQueryParams(body);
        validateRegisterParams(queryParams);

        final String account = queryParams.get("account");
        final String email = queryParams.get("email");
        final String password = queryParams.get("password");
        validateExistUser(account);
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        final HttpHeader location = HttpHeader.of(LOCATION.getValue(), "/index.html");
        return HttpResponse.of(HTTP11, REDIRECT, location);
    }

    private void validateExistUser(final String account) {
        if (InMemoryUserRepository.existByAccount(account)) {
            throw new DuplicateUserException();
        }
    }

    private void validateRegisterParams(final Map<String, String> queryParams) {
        if (!queryParams.containsKey("account")
                || !queryParams.containsKey("password")
                || !queryParams.containsKey("email")) {
            throw new IllegalArgumentException("회원가입 정보가 입력되지 않았습니다.");
        }
    }
}
