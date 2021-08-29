package nextstep.jwp.service;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.domain.Login;
import nextstep.jwp.http.response.GeneralResponse;
import nextstep.jwp.http.HttpRequest;

public class LoginService implements Service {

    public LoginService() {

    }

    public boolean login(HttpRequest httpRequest) throws IOException {
        final Map<String, String> queryOnURIS = httpRequest.body();
        final Login login = new Login(queryOnURIS);
        return login.isSuccess();
    }

    private String loginWindow(HttpRequest httpRequest) throws IOException {
        final GeneralResponse generalResponse = new GeneralResponse(httpRequest);
        return generalResponse.getResponse();
    }
}
