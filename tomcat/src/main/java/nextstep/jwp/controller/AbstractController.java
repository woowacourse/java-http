package nextstep.jwp.controller;

import static org.apache.coyote.request.line.HttpMethod.DELETE;
import static org.apache.coyote.request.line.HttpMethod.GET;
import static org.apache.coyote.request.line.HttpMethod.PATCH;
import static org.apache.coyote.request.line.HttpMethod.POST;
import static org.apache.coyote.request.line.HttpMethod.PUT;

import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        return true;
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.consistsOf(POST)) {
            doPost(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.consistsOf(GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.consistsOf(DELETE)) {
            doDelete(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.consistsOf(PUT)) {
            doDelete(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.consistsOf(PATCH)) {
            doDelete(httpRequest, httpResponse);
            return;
        }
        throw new NotFoundException();
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new NotFoundException();
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new NotFoundException();
    }

    protected void doDelete(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new NotFoundException();
    }

    protected void doPut(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new NotFoundException();
    }

    protected void doPatch(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new NotFoundException();
    }
}
