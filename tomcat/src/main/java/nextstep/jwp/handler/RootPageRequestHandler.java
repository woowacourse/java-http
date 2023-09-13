package nextstep.jwp.handler;

import static org.apache.catalina.servlet.response.HttpStatus.OK;

import org.apache.catalina.servlet.AbstractController;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.StatusLine;

public class RootPageRequestHandler extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String responseBody = "Hello world!";
        response.setStatusLine(new StatusLine(OK));
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.setMessageBody(responseBody);
    }
}
