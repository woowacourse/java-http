package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.framework.http.request.HttpRequest;
import nextstep.jwp.framework.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractController implements Controller {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.isGet()) {
            doGet(request, response);
        } else if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
    }
}
