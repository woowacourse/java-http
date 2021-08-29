package nextstep.jwp.service;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.domain.Login;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.response.LoginResponse;
import nextstep.jwp.util.HeaderLine;

public class LoginService implements Service {

    public LoginService() {

    }

    @Override
    public String process(HeaderLine headerLine) throws IOException {
        if (headerLine.method().equals("GET")) {
            return loginWindow(headerLine);
        }
        final Map<String, String> queryOnURIS = headerLine.body();
        final Login login = new Login(queryOnURIS);
        return LoginResult(login);
    }

    private String loginWindow(HeaderLine headerLine) throws IOException {
        final HttpResponse httpResponse = new HttpResponse(headerLine);
        return httpResponse.getResponse();
    }

    private String LoginResult(Login login) throws IOException {
        final LoginResponse loginResponse = new LoginResponse();
        if (login.isSuccess()) {
            return loginResponse.successResponse();
        }
        return loginResponse.failedResponse();
    }
}
