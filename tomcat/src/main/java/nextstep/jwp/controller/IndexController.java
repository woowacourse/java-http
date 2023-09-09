package nextstep.jwp.controller;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.util.FileReader;

public class IndexController extends AbstractController {
    private static final String RESOURCE = "/index.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        ResponseBody responseBody = new ResponseBody(FileReader.read(RESOURCE));
        StatusLine statusLine = new StatusLine(request.getRequestLine().getVersion(), HttpStatus.OK);
        response
                .statusLine(statusLine)
                .contentType(ContentType.HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .responseBody(responseBody);
    }

}
