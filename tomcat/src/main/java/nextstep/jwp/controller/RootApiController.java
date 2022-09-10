package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.exception.notfound.ControllerNotFoundException;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public class RootApiController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.ok("Hello world!");
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new ControllerNotFoundException(httpResponse + "\n 요청은 RootApiController에서 처리할 수 없습니다.");
    }
}
