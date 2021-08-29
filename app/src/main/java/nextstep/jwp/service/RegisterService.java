package nextstep.jwp.service;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.domain.SignUp;
import nextstep.jwp.response.RegisterResponse;
import nextstep.jwp.util.HeaderLine;

public class RegisterService implements Service {

    public RegisterService() {

    }

    @Override
    public String process(HeaderLine headerLine) throws IOException {
        final RegisterResponse registerResponse = new RegisterResponse();
        if (headerLine.method().equals("GET")) {
            return registerResponse.successResponse();
        }
        final Map<String, String> body = headerLine.body();
        SignUp signUp = new SignUp(body);
        return register(registerResponse, signUp);
    }

    private String register(RegisterResponse registerResponse, SignUp signUp) throws IOException {
        if (signUp.isAble()) {
            signUp.process();
            return registerResponse.createAccountResponse();
        }
        return registerResponse.failedResponse();
    }


}
