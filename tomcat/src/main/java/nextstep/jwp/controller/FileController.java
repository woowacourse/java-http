package nextstep.jwp.controller;

import nextstep.jwp.handle.ViewResolver;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class FileController extends AbstractController {

    private static final FileController fileController = new FileController();

    private FileController() {
    }

    public static FileController getInstance() {
        return fileController;
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        ViewResolver.renderPage(response, HttpStatus.OK, request.getUriPath().substring(1));
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }
}
