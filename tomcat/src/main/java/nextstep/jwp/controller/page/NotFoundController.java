package nextstep.jwp.controller.page;

import java.io.IOException;
import java.nio.file.Path;
import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.util.PathUtil;
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
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final Path path = PathUtil.findPathWithExtension(NOT_FOUND_URI, HTML);
        return HttpResponse.create(HttpStatus.NOT_FOUND, path);
    }
}
