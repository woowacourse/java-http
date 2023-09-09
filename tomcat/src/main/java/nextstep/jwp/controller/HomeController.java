package nextstep.jwp.controller;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;

public class HomeController implements Controller {
    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public HttpResponse service(HttpRequest request) {
        StatusLine statusLine = new StatusLine(request.getRequestLine().getVersion(), HttpStatus.OK);
        return HttpResponse.builder()
                .statusLine(statusLine)
                .contentType(ContentType.HTML.getValue())
                .contentLength(DEFAULT_MESSAGE.getBytes().length)
                .responseBody(new ResponseBody(DEFAULT_MESSAGE))
                .build();
    }


}
