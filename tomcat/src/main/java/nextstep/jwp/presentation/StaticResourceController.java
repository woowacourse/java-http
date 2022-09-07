package nextstep.jwp.presentation;

import java.nio.file.Path;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class StaticResourceController extends AbstractController {

    private static final StaticResourceController instance = new StaticResourceController();

    private StaticResourceController() {
    }

    public static StaticResourceController getInstance() {
        return instance;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Path path = StaticResource.ofRequest(request);

        response.setBody(path);
    }
}
