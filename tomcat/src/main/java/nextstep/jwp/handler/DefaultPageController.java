package nextstep.jwp.handler;

import org.apache.catalina.servlet.AbstractController;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.SupportFile;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;
import org.apache.coyote.http.vo.Url;

public class DefaultPageController extends AbstractController {

    private static final String supportUrl = "/";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setStatus(HttpStatus.OK)
                .setHeader(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType())
                .setBody("Hello world!");
    }

    @Override
    public boolean isSupported(final HttpRequest request) {
        return request.isRequestMethodOf(HttpMethod.GET) &&
                request.isUrl(Url.from(supportUrl));
    }
}
