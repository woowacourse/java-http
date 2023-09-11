package nextstep.jwp.controller.other;

import java.io.IOException;
import java.nio.file.Path;
import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.util.PathUtil;
import nextstep.jwp.util.ResponseBodyUtil;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class OtherController extends AbstractController {

    private OtherController() {
    }

    public static Controller create() {
        return new OtherController();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final Path path = PathUtil.findPath(request.getUri());
        final String responseBody = ResponseBodyUtil.alter(path);

        response.setStatusLine(HttpStatus.OK);
        response.setHeaders(path);
        response.setResponseBody(responseBody);
    }
}
