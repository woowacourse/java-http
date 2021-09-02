package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.utils.FileConverter;

public class FrontController extends AbstractController {

    @Override
    protected void doGet(Request request, Response response) throws IOException {
        String responseBody = FileConverter.fileToString(request.getUri());
        response.set200OK(request, responseBody);
    }

    @Override
    protected void doPost(Request request, Response response) throws IOException {
        throw new MethodNotAllowedException();
    }
}
