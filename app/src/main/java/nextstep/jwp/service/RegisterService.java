package nextstep.jwp.service;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.domain.SignUp;
import nextstep.jwp.http.HttpRequest;

public class RegisterService implements Service {

    public RegisterService() {

    }

    public boolean isSuccess(HttpRequest httpRequest) throws IOException {
        final Map<String, String> body = httpRequest.body();
        SignUp signUp = new SignUp(body);
        if (signUp.isAble()) {
            signUp.process();
            return true;
        }
        return false;
    }
}
