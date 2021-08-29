package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.response.LoginResponse;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.util.HeaderLine;

public class LoginController implements Controller {

    @Override
    public String process(HeaderLine headerLine) throws IOException {
        final LoginService service = new LoginService();
        if (headerLine.method().equals("GET")) {
            return loginWindow(headerLine);
        }
        final boolean isSuccess = service.login(headerLine);

        final LoginResponse loginResponse = new LoginResponse();
        if (isSuccess) {
            return loginResponse.successResponse();
        }
        return loginResponse.failedResponse();
    }

    private String loginWindow(HeaderLine headerLine) throws IOException {
        final HttpResponse httpResponse = new HttpResponse(headerLine);
        return httpResponse.getResponse();
    }

}
