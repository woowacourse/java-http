package nextstep.jwp.presentation;

import java.nio.file.Path;
import org.apache.coyote.http11.common.HttpRequest;
import org.apache.coyote.http11.common.HttpResponse;
import org.apache.coyote.http11.util.StaticResource;

public class StaticResourceController extends AbstractController {

    private static final StaticResourceController INSTANCE = new StaticResourceController();

    private StaticResourceController() {
    }

    public static StaticResourceController getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Path path = StaticResource.ofRequest(request);

        response.setBody(path);
    }
}
