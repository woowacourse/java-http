package nextstep.jwp.controller;

import nextstep.jwp.handler.HttpRequest;
import nextstep.jwp.handler.HttpResponse;
import nextstep.jwp.util.File;
import nextstep.jwp.util.FileReader;

public class ErrorController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        File file = FileReader.readErrorFile("/404.html");
        httpResponse.notFound("/404.html", file);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        File file = FileReader.readErrorFile("/404.html");
        httpResponse.notFound("/404.html", file);
    }
}
