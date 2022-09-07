package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.support.ResourceFindUtils;

public class LoginFailedController implements Controller {

    private static final String PATH = "/401.html";

    @Override
    public HttpResponse service(HttpRequest request) {
        final String responseBody = ResourceFindUtils.getResourceFile(PATH);

        return new HttpResponse.Builder()
                .status(HttpStatus.FOUND)
                .responseBody(responseBody)
                .build();
    }
}
