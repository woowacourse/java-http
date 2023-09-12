package nextstep.jwp.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.TargetPath;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.header.ContentType;

public class HelloController implements Controller {

    private static final TargetPath SUPPORTED_PATH = new TargetPath("/");

    @Override
    public boolean supports(HttpRequest httpRequest) {
        return httpRequest.getTarget().getPath().equals(SUPPORTED_PATH) && httpRequest.getMethod().isGet();
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setContentType(new ContentType("text/html"));
        httpResponse.setBody("Hello world!");
    }
}
