package nextstep.jwp.handler;

import org.apache.coyote.http11.handler.RequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.StatusLine;

public class RootPageRequestHandler implements RequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.startLine().uri().path().equals("/");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String responseBody = "Hello world!";
        StatusLine statusLine = new StatusLine(HttpStatus.OK);
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.put("Content-Type", "text/html;charset=utf-8");
        return new HttpResponse(statusLine, responseHeaders, responseBody);
    }
}
