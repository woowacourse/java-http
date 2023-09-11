package nextstep.jwp.controller.page;

import java.io.IOException;
import java.nio.file.Path;
import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.util.PathUtil;
import nextstep.jwp.util.ResponseBodyUtil;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class NotFoundController extends AbstractController {

    private NotFoundController() {
    }

    public static Controller create() {
        return new NotFoundController();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final Path path = PathUtil.findPathWithExtension(NOT_FOUND_URI, HTML);

        response.setStatusLine(HttpStatus.NOT_FOUND);
        response.setHeaders(path);
        response.setResponseBody(ResponseBodyUtil.alter(path));
    }
}
