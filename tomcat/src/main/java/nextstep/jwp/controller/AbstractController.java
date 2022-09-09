package nextstep.jwp.controller;

import nextstep.jwp.exception.InvalidHttpMethodException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        RequestMapping requestMapping = new RequestMapping();

        if (request.isGet()) {
            doGet(request, response);
        }
        if (request.isPost()) {
            doPost(request, response);
        }
        throw new InvalidHttpMethodException("유효하지 않은 HTTP method 입니다.");
    }

    protected HttpResponse doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */
        return null;
    }

    protected HttpResponse doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */
        return null;
    }
}
