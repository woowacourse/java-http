package nextstep.jwp.presentation;

import static org.apache.coyote.http11.support.HttpStatus.OK;

import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;
import java.util.LinkedHashMap;

public class MainController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return new HttpResponse(OK, new HttpHeaders(new LinkedHashMap<>()), "Hello world!");
    }
}
