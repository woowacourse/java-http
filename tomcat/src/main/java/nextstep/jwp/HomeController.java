package nextstep.jwp;

import java.io.IOException;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.HttpExtensionType;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

public class HomeController extends AbstractController {

    private static final String DEFAULT_CONTENT = "Hello world!";

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final ResponseBody responseBody = ResponseBody.of(HttpExtensionType.HTML.getExtension(), DEFAULT_CONTENT);
        return HttpResponse.of(HttpStatusCode.OK, responseBody);
    }
}
