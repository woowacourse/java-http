package nextstep.jwp.presentation;

import static org.apache.coyote.http11.ContentType.TEXT_CSS;
import static org.apache.coyote.http11.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.HttpStatus.OK;

import java.util.Optional;
import nextstep.jwp.common.ResourceLoader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(OK);
        Optional<String> acceptHeader = request.getHeader("Accept");
        if (acceptHeader.isPresent() && acceptHeader.get().contains("text/css")) {
            response.setContentType(TEXT_CSS);
            response.setBody(ResourceLoader.load("static" + request.uri()));
            return;
        }
        response.setContentType(TEXT_HTML);
        response.setBody(ResourceLoader.load("static" + request.uri()));
    }
}
