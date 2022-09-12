package nextstep.jwp.controller;

import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.util.FileReader;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            String uri = httpRequest.getUri();
            String responseBody = FileReader.read(uri);
            httpResponse.ok()
                    .contentType(ContentType.from(uri))
                    .body(responseBody)
                    .flushBuffer();
        } catch (NotFoundException e) {
            httpResponse.found()
                    .location("/404.html")
                    .flushBuffer();
        }
    }
}
