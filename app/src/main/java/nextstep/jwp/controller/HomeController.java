package nextstep.jwp.controller;

import nextstep.jwp.utils.ContentType;
import nextstep.jwp.utils.FileReader;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String content = FileReader.file("/index.html");

        response.writeStatusLine(HttpStatus.OK);
        response.writeHeaders(content, ContentType.HTML);
        response.writeBody(content);
    }
}
