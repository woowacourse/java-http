package nextstep.jwp.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.util.FileReader;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String resource =  request.getRequestLine().getRequestURI().getResourcePath();
        ResponseBody responseBody = new ResponseBody(FileReader.read(resource));
        StatusLine statusLine = new StatusLine(request.getRequestLine().getVersion(), HttpStatus.OK);
        response
                .statusLine(statusLine)
                .contentType(request.contentType().getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .responseBody(responseBody);
    }

}
