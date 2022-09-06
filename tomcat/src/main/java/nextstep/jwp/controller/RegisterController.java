package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController implements Controller {

    @Override
    public HttpResponse process(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isGet()) {
            return HttpResponse.ok("/register.html");
        }
        if (httpRequest.isPost()) {
            doPost(httpRequest);
        }
        return HttpResponse.notFound();
    }

    private void doPost(HttpRequest httpRequest) {
    }
}
