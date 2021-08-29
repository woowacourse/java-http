package nextstep.jwp.service;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.model.Login;
import nextstep.jwp.util.HeaderLine;
import nextstep.jwp.util.HttpResponse;

public class LoginService implements Service {

    public LoginService() {

    }

    @Override
    public String process(HeaderLine headerLine) throws IOException {
        if (!headerLine.isOnQuery()) {
            HttpResponse httpResponse = new HttpResponse(headerLine);
            httpResponse.getResponse();
        }
        Map<String, String> queryOnURIS = headerLine.queryOnURI();
        Login login = new Login(queryOnURIS);
        if(login.isSuccess()) {
            return successLogin();
        }
        return failLogin();
    }

    private String successLogin() {
        return "success";
    }

    private String failLogin() {
        return "false";
    }
}
