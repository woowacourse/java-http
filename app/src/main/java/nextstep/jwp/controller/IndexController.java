package nextstep.jwp.controller;

import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.common.ContentType.HTML;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.OK;

public class IndexController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
        response.setStatus(OK);
        response.setContentType(HTML.value());
        mv.setViewName("/index");
    }
}
