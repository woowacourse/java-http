package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.domain.Login;
import nextstep.jwp.domain.Token;
import nextstep.jwp.domain.UUIDStrategy;
import nextstep.jwp.http.request.HttpRequest;

public class LoginService implements Service {

    public boolean login(HttpRequest httpRequest) {
        final Map<String, String> queryOnURIS = httpRequest.parsedBody();
        final Login login = new Login(queryOnURIS);
        return login.isSuccess();
    }

    public String token(HttpRequest httpRequest) {
        UUIDStrategy uuidStrategy = new UUIDStrategy();
        Token token = Token.fromStrategy(uuidStrategy);
        return token.value();
    }
}
