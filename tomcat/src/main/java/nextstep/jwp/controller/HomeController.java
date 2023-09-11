package nextstep.jwp.controller;

import common.http.AbstractController;
import common.http.Request;
import common.http.Response;

import static common.http.ContentType.HTML;
import static common.http.HttpStatus.OK;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(Request request, Response response) {
        response.addVersionOfTheProtocol(request.getVersionOfTheProtocol());
        response.addHttpStatus(OK);
        response.addContentType(HTML);
        response.addBody("Hello world!");
    }
}
