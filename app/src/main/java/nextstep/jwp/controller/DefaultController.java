package nextstep.jwp.controller;

import nextstep.jwp.model.httpmessage.common.ContentType;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.util.FileUtils;
import nextstep.jwp.view.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.common.HttpHeaderType.CONTENT_TYPE;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.OK;

public class DefaultController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultController.class);

    /*
    정적 페이지는 url에 맞는 파일을 출력한다.
     */

    @Override
    protected void doGet(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
//        response.forward("/index.html");

//        mv.setStatus(OK);
//        LOG.debug("Response status : {}", OK);

        response.forward("/index.html");
//
//        ContentType contentType = ContentType.of(url).get();
//        response.addHeader(CONTENT_TYPE, contentType.value());
//        LOG.debug("Response content type : {}", contentType.value());
//
//        String messageBody = FileUtils.readFileOfUrl(url);
//        mv.getModel().put("body", messageBody);
//        LOG.debug("Response messageBody : {}", messageBody);
    }
}
