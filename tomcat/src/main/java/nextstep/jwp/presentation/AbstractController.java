package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractController implements Controller {
    protected static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getHttpMethod().equals(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.getHttpMethod().equals(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        throw new IllegalArgumentException("GET, POST 이외의 메서드입니다. ");
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        log.info("RootController -> request : {}, response : {} ", request, response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
