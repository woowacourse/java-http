package nextstep.jwp.framework.controller.custom;

import java.util.Map;
import nextstep.jwp.framework.controller.CustomController;
import nextstep.jwp.framework.infrastructure.http.content.ContentType;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.web.application.UserService;

public class RegisterController extends CustomController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getMethod();
        String url = httpRequest.getUrl();
        return url.equals("/register") &&
            (httpMethod.equals(HttpMethod.GET) || httpMethod.equals(HttpMethod.POST));
    }

    protected HttpResponse doGet(HttpRequest httpRequest) {
        String url = "/register.html";
        return new HttpResponse.Builder()
            .protocol(httpRequest.getProtocol())
            .httpStatus(HttpStatus.OK)
            .contentType(ContentType.find(url))
            .responseBody(readFile(url))
            .build();
    }

    protected HttpResponse doPost(HttpRequest httpRequest) {
        Map<String, String> attributes = httpRequest.getContentAsAttributes();
        try {
            userService.register(
                attributes.get("account"),
                attributes.get("password"),
                attributes.get("email")
            );
            String redirectUrl = "/index.html";
            return new HttpResponse.Builder()
                .protocol(httpRequest.getProtocol())
                .httpStatus(HttpStatus.FOUND)
                .contentType(ContentType.find(redirectUrl))
                .location(redirectUrl)
                .responseBody(readFile(redirectUrl))
                .build();
        } catch (RuntimeException runtimeException) {
            String errorUrl = "/401.html";
            return new HttpResponse.Builder()
                .protocol(httpRequest.getProtocol())
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .contentType(ContentType.find(errorUrl))
                .responseBody(readFile(errorUrl))
                .build();
        }
    }
}
