package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.response.RegisterResponse;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.util.HeaderLine;

public class RegisterController implements Controller {

    @Override
    public String process(HeaderLine headerLine) throws IOException {
        final RegisterService registerService = new RegisterService();
        final RegisterResponse registerResponse = new RegisterResponse();
        if (headerLine.method().equals("GET")) {
            return registerResponse.successResponse();
        }
        final boolean isSuccess = registerService.isSuccess(headerLine);
        return createResultResponse(registerResponse, isSuccess);
    }

    private String createResultResponse(RegisterResponse registerResponse, boolean isSuccess)
        throws IOException {
        if (isSuccess) {
            return registerResponse.createAccountResponse();
        }
        return registerResponse.failedResponse();
    }
}
