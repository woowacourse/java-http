package nextstep.jwp.controller;

import nextstep.jwp.handler.DashboardException;
import nextstep.jwp.utils.FileFinder;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

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
