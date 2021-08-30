package nextstep.jwp.controller;

import nextstep.jwp.handler.HttpRequest;
import nextstep.jwp.handler.HttpResponse;
import nextstep.jwp.util.File;
import nextstep.jwp.util.FileReader;

public class ErrorController extends AbstractController {

    public static final String NOT_FOUND_HTML = "/404.html";

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        File file = FileReader.readErrorFile(NOT_FOUND_HTML);
        httpResponse.notFound(NOT_FOUND_HTML, file);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        File file = FileReader.readErrorFile(NOT_FOUND_HTML);
        httpResponse.notFound(NOT_FOUND_HTML, file);
    }
}
