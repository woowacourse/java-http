package nextstep.jwp.controller;

import nextstep.jwp.exception.controller.InvalidPostRequestException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexController extends AbstractController{

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @Override
    protected View doGet(HttpRequest request, HttpResponse response) {
        log.debug("Index - HTTP GET Request");

        response.setLine(HttpStatus.OK);
        response.setContentType("text/html;charset=utf-8");

        return new View(request.getPath());
    }

    @Override
    protected View doPost(HttpRequest request, HttpResponse response) {
       log.debug("Index - HTTP POST Request");

       throw new InvalidPostRequestException();
    }
}
