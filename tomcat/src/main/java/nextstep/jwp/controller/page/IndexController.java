package nextstep.jwp.controller.page;

import java.io.IOException;
import java.nio.file.Path;
import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.util.PathUtil;
import nextstep.jwp.util.ResponseBodyUtil;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatusLine;

public class IndexController extends AbstractController {

    private IndexController() {
    }

    public static Controller create() {
        return new IndexController();
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final Path path = PathUtil.findPath(request.getUri());
        return HttpResponse.create(HttpStatus.OK, path);
    }
}
