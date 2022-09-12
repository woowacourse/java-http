package nextstep.jwp.controller;

import org.apache.coyote.http11.exception.StaticFileNotFoundException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestUri;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.util.StaticFileUtil;

public class StaticFileController extends AbstractController {

    @Override
    protected HttpResponse handleGet(final HttpRequest request) {
        RequestUri requestUri = request.getRequestUri();

        try {
            String file = StaticFileUtil.readFile(requestUri.getPath());
            return new HttpResponse.Builder()
                    .contentType(requestUri.getExtension())
                    .body(file)
                    .build();

        } catch (StaticFileNotFoundException e) {
            return notFound();
        }
    }
}
