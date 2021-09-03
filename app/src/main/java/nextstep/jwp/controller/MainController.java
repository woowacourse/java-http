package nextstep.jwp.controller;

import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nextstep.jwp.model.httpmessage.common.ContentType.HTML;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.OK;

public class MainController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response, ModelAndView mv) {
        response.setResponseLine(OK, request.getProtocol());
        mv.setStatus(OK);
        LOG.debug("Response status : {}", OK);

        response.setContentType(HTML.value());
        LOG.debug("Response content type : {}", HTML);

        String body = "Hello world!";

        response.setContentLength(body.getBytes().length);
        LOG.debug("Response content length : {}", body.getBytes().length);

        mv.getModel().put("body", body);
    }
}
