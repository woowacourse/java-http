package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.service.RegisterService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController implements Controller {

    @Override
    public HttpResponse process(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isGet()) {
            return HttpResponse.ok("/register.html");
        }
        if (httpRequest.isPost()) {
            return doPost(httpRequest);
        }
        return HttpResponse.notFound();
    }

    private HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        RegisterService.register(httpRequest.getBody());
        return HttpResponse.ok("/index.html");
    }
}
