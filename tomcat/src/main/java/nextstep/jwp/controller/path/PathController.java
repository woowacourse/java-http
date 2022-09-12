package nextstep.jwp.controller.path;

import nextstep.jwp.controller.Controller;
import web.request.HttpRequest;
import web.response.HttpResponse;

public abstract class PathController implements Controller {

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        doGet(httpRequest, httpResponse);
        doPost(httpRequest, httpResponse);
    }

    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.getRequestLine().getMethod().equals("GET")) {
            throw new UnsupportedOperationException("[ERROR] 지원하지 않는 Http Method 입니다.");
        }
    }

    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.getRequestLine().getMethod().equals("POST")) {
            throw new UnsupportedOperationException("[ERROR] 지원하지 않는 Http Method 입니다.");
        }
    }

    public String getPath() {
        return "/";
    }
}
