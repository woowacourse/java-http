package nextstep.jwp;

import java.io.IOException;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.ExtensionType;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.http11.response.StatusLine;

public class HomeController extends AbstractController {

    private static final String DEFAULT_CONTENT = "Hello world!";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final ResponseBody responseBody = ResponseBody.of(ExtensionType.HTML.getExtension(), DEFAULT_CONTENT);
        final ResponseHeader responseHeader = ResponseHeader.from(responseBody);

        response.setStatusLine(new StatusLine(HttpStatusCode.OK));
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
    }
}
