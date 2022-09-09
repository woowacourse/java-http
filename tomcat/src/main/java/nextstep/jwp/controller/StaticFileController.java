package nextstep.jwp.controller;

import java.io.File;
import java.net.URL;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Headers;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponse.ResponseBuilder;
import org.apache.coyote.http11.response.Status;

public class StaticFileController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        String url = request.getUrl();
        if (hasMatchedStaticFile(url)) {
            final String body = readResourceBody(url);
            final Headers headers = readResourceHeader(url, body);

            return new ResponseBuilder().status(Status.OK)
                    .headers(headers)
                    .body(body)
                    .build();
        }

        return new ResponseBuilder().status(Status.NOT_FOUND)
                .build();
    }

    private boolean hasMatchedStaticFile(final String url) {
        final URL resource = getClass().getClassLoader().getResource("static" + url);
        return resource != null && new File(resource.getFile()).isFile();
    }
}
