package nextstep.jwp.controller.page;

import java.io.IOException;
import java.nio.file.Path;
import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.util.PathUtil;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class InternalServerErrorController extends AbstractController {

    public static HttpResponse create(final HttpRequest request) throws IOException {
        final Path path = PathUtil.findPathWithExtension(INTERNAL_SERVER_ERROR_URI, HTML);
        return HttpResponse.create(HttpStatus.INTERNAL_SERVER_ERROR, path);
    }
}
