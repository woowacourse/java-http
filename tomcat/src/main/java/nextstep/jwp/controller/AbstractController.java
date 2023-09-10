package nextstep.jwp.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.coyote.common.ContentType.HTML;
import static org.apache.coyote.response.HttpStatus.FOUND;

public class AbstractController implements Controller {

    protected static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {

        try {
            if (request.getRequestLine().isGetMethod()) {
                doGet(request, response);
                return;
            }
            if (request.getRequestLine().isPostMethod()) {
                doPost(request, response);
            }
        } catch (Exception e) {
            response.setStatus(FOUND);
            response.setContentType(HTML);
            response.addHeader("Location", "/500.html");
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
