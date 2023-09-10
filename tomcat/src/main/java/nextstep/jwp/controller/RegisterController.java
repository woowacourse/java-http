package nextstep.jwp.controller;

import static org.apache.coyote.HttpStatus.FOUND;
import static org.apache.coyote.header.HttpHeaders.LOCATION;
import static org.apache.coyote.header.HttpMethod.POST;

import java.util.Map;
import nextstep.jwp.application.RegisterService;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.handler.Controller;

public class RegisterController implements Controller {

    private final RegisterService registerService = new RegisterService();

    @Override
    public boolean support(HttpRequest request) {
        return request.httpMethod().equals(POST) && request.requestUrl().equals("/register");
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        Map<String, String> requestBody = request.getRequestBody();
        registerService.register(
                requestBody.get("account"),
                requestBody.get("email"),
                requestBody.get("password")
        );

        response.setVersion(request.protocolVersion());
        response.setStatus(FOUND);
        response.addHeader(LOCATION, "index.html");
    }
}
