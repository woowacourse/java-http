package nextstep.jwp;

import java.io.IOException;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.FileExtractor;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.http11.response.StatusLine;

public class DefaultController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final String resource = request.getRequestPath().getResource();
        final ResponseBody responseBody = FileExtractor.extractFile(resource);
        final ResponseHeader responseHeader = ResponseHeader.from(responseBody);

        response.setStatusLine(new StatusLine(HttpStatusCode.OK));
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
    }
}
