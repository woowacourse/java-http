package nextstep.jwp.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticFileController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (hasMatchedStaticFile(request.getRequestUrl())) {
            final URL resource = getClass().getClassLoader().getResource("static" + request.getRequestUrl());
            final Path path = new File(resource.getFile()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));
            headers.put(CONTENT_TYPE, Files.probeContentType(path));

            return httpResponse.create200Response(headers, responseBody)
        }
    }


    private boolean hasMatchedStaticFile(final String url) {
        final URL resource = getClass().getClassLoader().getResource("static" + url);
        return resource != null && new File(resource.getFile()).isFile();
    }
}
