package nextstep.jwp.controller.other;

import java.io.IOException;
import java.nio.file.Path;
import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.util.PathUtil;
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
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final String uri = request.getUri();
        final Path path = PathUtil.findPath(uri);

        return HttpResponse.create(HttpStatus.OK, path);
    }
}
