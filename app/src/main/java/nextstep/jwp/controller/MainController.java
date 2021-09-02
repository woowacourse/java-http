package nextstep.jwp.controller;

import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.common.ContentType.HTML;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.OK;

public class MainController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
        response.setStatus(OK);
        LOG.debug("Response status : {}", OK);

        String html = HTML.value();
        response.setContentType(html);
        LOG.debug("Response content type : {}", html);

        String body = "Hello world!";

        response.setContentLength(body.getBytes().length);
        LOG.debug("Response content length : {}", body.getBytes().length);

        mv.getModel().put("body", body);
    }
}
