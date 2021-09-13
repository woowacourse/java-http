package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.utils.ContentType;
import nextstep.jwp.utils.FileReader;
import nextstep.jwp.utils.Resources;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String content = FileReader.file(Resources.INDEX.getResource());

        response.addHeaders("Content-Type", ContentType.HTML.getContentType());
        response.addHeaders("Content-Length", String.valueOf(content.getBytes().length));

        response.writeStatusLine(HttpStatus.OK);
        response.writeHeaders();
        response.writeBody(content);
    }
}
