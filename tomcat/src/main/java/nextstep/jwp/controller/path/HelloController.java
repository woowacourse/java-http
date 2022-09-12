package nextstep.jwp.controller.path;

import web.HttpMethod;
import web.request.HttpRequest;
import web.response.HttpResponse;
import web.response.StatusLine;

public class HelloController extends PathController {

    private static HelloController instance = new HelloController();

    public static HelloController getInstance() {
        if (instance == null) {
            instance = new HelloController();
        }
        return instance;
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        HttpMethod method = httpRequest.getRequestLine().getMethod();
        if (method.equals(HttpMethod.GET)) {
            String body = "Hello World!";
            httpResponse.setBody(body);
            httpResponse.setStatusLine(new StatusLine("HTTP/1.1", "200", "OK"));
            httpResponse.putHeader("Content-Type", "text/html;charset=utf-8");
            httpResponse.putHeader("Content-Length", String.valueOf(body.getBytes().length));
        }
    }
}
