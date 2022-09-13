package nextstep.jwp.presentation;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.support.AbstractController;
import org.apache.coyote.util.FileUtils;

public class HomeController extends AbstractController {

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        String body = FileUtils.readAllBytes(request.getPath().getValue());
        response.forward(request.getPath());
        response.setBody(body);
        response.write();
    }
}
