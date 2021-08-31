package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.domain.SignUp;
import nextstep.jwp.http.request.HttpRequest;

public class RegisterService implements Service {

    public boolean isSuccess(HttpRequest httpRequest) {
        final Map<String, String> body = httpRequest.parsedBody();
        SignUp signUp = new SignUp(body);
        if (signUp.isAble()) {
            signUp.process();
            return true;
        }
        return false;
    }
}
