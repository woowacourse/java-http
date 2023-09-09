package nextstep.jwp.controller;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.util.FileReader;

public class ResourceController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        String resource =  request.getRequestLine().getRequestURI().getResourcePath();
        ResponseBody responseBody = new ResponseBody(FileReader.read(resource));
        StatusLine statusLine = new StatusLine(request.getRequestLine().getVersion(), HttpStatus.OK);
        return HttpResponse.builder()
                .statusLine(statusLine)
                .contentType(request.contentType().getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .responseBody(responseBody)
                .build();
    }

}
