package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.response.RegisterResponse;
import nextstep.jwp.service.RegisterService;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final RegisterResponse registerResponse = new RegisterResponse();
        final boolean isSuccess = registerService.isSuccess(request);
        final String result = createResultResponse(registerResponse, isSuccess);
        response.setResponse(result);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final RegisterResponse registerResponse = new RegisterResponse();
        final String result = registerResponse.successResponse();
        response.setResponse(result);
    }

    private String createResultResponse(RegisterResponse registerResponse, boolean isSuccess)
        throws IOException {
        if (isSuccess) {
            return registerResponse.createAccountResponse();
        }
        return registerResponse.failedResponse();
    }
}
