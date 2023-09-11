package nextstep.jwp.controller.page;

import java.io.IOException;
import java.nio.file.Path;
import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.util.PathUtil;
import nextstep.jwp.util.ResponseBodyUtil;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class InternalServerErrorController extends AbstractController {

    public static void create(final HttpRequest request, final HttpResponse response) throws IOException {
        final Path path = PathUtil.findPathWithExtension(INTERNAL_SERVER_ERROR_URI, HTML);

        response.setStatusLine(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setHeaders(path);
        response.setResponseBody(ResponseBodyUtil.alter(path));
    }
}
