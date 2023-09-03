package nextstep.jwp.handler;

import org.apache.catalina.servlet.handler.RequestHandler;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.HttpStatus;
import org.apache.catalina.servlet.response.ResponseHeaders;
import org.apache.catalina.servlet.response.StatusLine;

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
