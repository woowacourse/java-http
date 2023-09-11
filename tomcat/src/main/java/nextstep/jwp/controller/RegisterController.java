package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController<UserService> {

    public RegisterController(UserService userService) {
        super(userService);
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestLine requestLine = httpRequest.getRequestLine();

        httpResponse.setHttpStatus(HttpStatus.OK)
                .setPath(requestLine.getPath());
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestBody requestBody = httpRequest.getRequestBody();

        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");

        service.save(account, password, email);
    }
}
