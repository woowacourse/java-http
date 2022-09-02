package org.apache.coyote.http11.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;

public class LoginHandler implements Handler {

    private final HttpRequest httpRequest;

    public LoginHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        Map<String, String> queryParams = httpRequest.getQueryParams();
        validateUser(queryParams);
        String responseBody = FileReader.getFile("/login.html", getClass());
        HttpResponse httpResponse = HttpResponse.from(ContentType.HTML, responseBody);
        return httpResponse.getResponse();
    }

    private void validateUser(final Map<String, String> queryParams) {
        String account = queryParams.get("account");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("입력한 아이디의 유저가 존재하지 않습니다."));
        validateUserPassword(queryParams, user);
    }

    private void validateUserPassword(final Map<String, String> queryParams, final User user) {
        String password = queryParams.get("password");
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
