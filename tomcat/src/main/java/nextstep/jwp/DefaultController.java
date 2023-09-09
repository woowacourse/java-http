package nextstep.jwp;

import java.io.IOException;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.FileExtractor;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

public class DefaultController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final String resource = request.getRequestPath().getResource();
        final ResponseBody responseBody = FileExtractor.extractFile(resource);
        return HttpResponse.of(HttpStatusCode.OK, responseBody);
    }
}
