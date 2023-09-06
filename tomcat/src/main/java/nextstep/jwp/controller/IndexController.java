package nextstep.jwp.controller;

import nextstep.jwp.FileFinder;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class IndexController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new IllegalArgumentException();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setBody(FileFinder.getFileContent(request.getUri()));
        response.setHttpStatus(HttpStatus.OK);
    }
}
