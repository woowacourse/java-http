package org.apache.controller.path;

import org.apache.request.HttpRequest;
import org.apache.request.RequestLine;
import org.apache.response.HttpResponse;
import org.apache.response.StatusLine;

public class HelloController implements PathController {

    private static HelloController helloController = new HelloController();

    private final String path = "/";

    public static HelloController getInstance() {
        return helloController;
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        RequestLine requestLine = httpRequest.getRequestLine();
        String method = requestLine.getMethod();

        if (method.equals("GET")) {
            String body = "Hello World!";
            httpResponse.setBody(body);
            httpResponse.setStatusLine(new StatusLine("HTTP/1.1", "200", "OK"));
            httpResponse.putHeader("Content-Type", "text/html;charset=utf-8");
            httpResponse.putHeader("Content-Length", String.valueOf(body.getBytes().length));
        }
    }

    @Override
    public String getPath() {
        return path;
    }
}
