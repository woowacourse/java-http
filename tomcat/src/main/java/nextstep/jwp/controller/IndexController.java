package nextstep.jwp.controller;

import nextstep.jwp.FileFinder;
import org.apache.coyote.http11.DashboardException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class IndexController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new DashboardException(HttpStatus.METHOD_NOT_ALLOWED.code);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setBody(FileFinder.getFileContent(request.getPath()));
        response.setHttpStatus(HttpStatus.OK);
    }
}
