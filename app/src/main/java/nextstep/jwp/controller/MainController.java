package nextstep.jwp.controller;

import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.common.HttpHeaderType.CONTENT_TYPE;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.OK;

public class MainController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    /*
    메인 페이지는 "Hello world!"를 출력한다. (Plain Text)
     */
    @Override
    protected void doGet(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
        response.forwardBody("Hello world!");

//        mv.setStatus(OK);
//        LOG.debug("Response status : {}", OK);
//
//        response.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
//        LOG.debug("Response content type : {}", "text/html;charset=utf-8");
//
//        String messageBody = "Hello world!";
//        mv.getModel().put("body", messageBody);
//        LOG.debug("Response messageBody : {}", messageBody);
    }
}
