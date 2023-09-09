package nextstep.jwp.controller;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.util.FileReader;

public class IndexController implements Controller {
    private static final String RESOURCE = "/index.html";

    @Override
    public HttpResponse service(HttpRequest request) {
        ResponseBody responseBody = new ResponseBody(FileReader.read(RESOURCE));
        StatusLine statusLine = new StatusLine(request.getRequestLine().getVersion(), HttpStatus.OK);
        return HttpResponse.builder()
                .statusLine(statusLine)
                .contentType(ContentType.HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .responseBody(responseBody)
                .build();
    }

}
