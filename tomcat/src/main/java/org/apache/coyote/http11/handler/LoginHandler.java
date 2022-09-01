package org.apache.coyote.http11.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.ResourceUtil;

public class LoginHandler implements Handler {

    private final HttpRequest httpRequest;

    public LoginHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        Map<String, String> queryParams = httpRequest.getQueryParams();
        validateUser(queryParams);
        String responseBody = ResourceUtil.getResponseBody("/login.html", getClass());
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
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
