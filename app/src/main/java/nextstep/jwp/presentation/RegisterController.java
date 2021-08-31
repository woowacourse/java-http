package nextstep.jwp.presentation;

import java.io.IOException;
import nextstep.jwp.application.RegisterService;
import nextstep.jwp.web.StaticResourceReader;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.ContentType;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.StatusCode;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String responseBody =
            new StaticResourceReader(request.getUrl() + ContentType.HTML.getExtension()).content();

        response.setStatusLine(StatusCode.OK);
        response.addHeader("Content-Type", ContentType.HTML.getValue());
        response.addHeader("Content-Length", responseBody.getBytes().length + " ");
        response.addBody(responseBody);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        registerService.register(
            request.getAttribute("account"),
            request.getAttribute("password"),
            request.getAttribute("email")
        );

        response.setStatusLine(StatusCode.FOUND);
        response.addHeader("Location", "/index.html");
    }
}
