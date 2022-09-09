package nextstep.jwp.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Headers;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponse.ResponseBuilder;
import org.apache.coyote.http11.response.Status;

public class StaticFileController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (hasMatchedStaticFile(request.getRequestUrl())) {
            final URL resource = getClass().getClassLoader().getResource("static" + request.getRequestUrl());
            final Path path = new File(resource.getFile()).toPath();
            final String body = new String(Files.readAllBytes(path));

            Headers headers = new Headers();
            headers.setContentType(Files.probeContentType(path));
            headers.setContentLength(body.getBytes().length);

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
