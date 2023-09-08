package nextstep.jwp.controller;

import nextstep.jwp.FileFinder;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MyException;

public class IndexController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new MyException(HttpStatus.BAD_REQUEST.code);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setBody(FileFinder.getFileContent(request.getUri()));
        response.setHttpStatus(HttpStatus.OK);
    }
}
