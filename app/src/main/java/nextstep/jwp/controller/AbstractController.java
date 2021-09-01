package nextstep.jwp.controller;

import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractController implements Controller {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        if (request.isGet()) {
            doGet(request, response);
        } else if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) {
    }
}
