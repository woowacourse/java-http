package nextstep.jwp.controller;

import nextstep.jwp.controller.modelview.ModelView;
import nextstep.jwp.httpmessage.httprequest.HttpRequest;
import nextstep.jwp.httpmessage.httpresponse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(HelloController.class);

    @Override
    protected ModelView doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setBody("Hello World!");
        return new ModelView("/");
    }

    @Override
    protected ModelView doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        LOG.info("제공하지 않는 Method입니다.");
        throw new UnsupportedOperationException("제공하지 않는 Method입니다.");
    }
}
