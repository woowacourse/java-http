package nextstep.jwp.controller;

import nextstep.jwp.FileFinder;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MyException;

public class IndexController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new MyException(HttpStatus.METHOD_NOT_ALLOWED.code);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setBody(FileFinder.getFileContent(request.getPath()));
        response.setHttpStatus(HttpStatus.OK);
    }
}
