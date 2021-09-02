package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.utils.FileConverter;

public class FrontController extends AbstractController {

    @Override
    protected void doGet(Request request, Response response) throws IOException {
        try {
            String responseBody = FileConverter.fileToString(request.getUri());
            response.set200OK(request, responseBody);
        } catch (NullPointerException exception) {
            throw new NotFoundException();
        }
    }

    @Override
    protected void doPost(Request request, Response response) {
        throw new MethodNotAllowedException();
    }
}
