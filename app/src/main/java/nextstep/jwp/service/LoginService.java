package nextstep.jwp.service;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.domain.Login;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.util.HeaderLine;

public class LoginService implements Service {

    public LoginService() {

    }

    public boolean login(HeaderLine headerLine) throws IOException {
        final Map<String, String> queryOnURIS = headerLine.body();
        final Login login = new Login(queryOnURIS);
        return login.isSuccess();
    }

    private String loginWindow(HeaderLine headerLine) throws IOException {
        final HttpResponse httpResponse = new HttpResponse(headerLine);
        return httpResponse.getResponse();
    }
}
