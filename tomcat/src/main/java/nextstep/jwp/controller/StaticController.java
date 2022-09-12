package nextstep.jwp.controller;

import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.StaticFile;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;
import org.apache.coyote.http11.response.HttpStatus;

public class StaticController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request, HttpResponseBuilder responseBuilder) throws IOException {
        String path = request.path();
        if (path.equals("/") || path.isEmpty()) {
            return responseBuilder.status(HttpStatus.OK)
                    .body("hello world!")
                    .build();
        }
        String body = new String(Files.readAllBytes(StaticFile.findByPath(path)
                .toPath()));
        return responseBuilder.status(HttpStatus.OK)
                .setHeader("Content-Type", ContentType.fromUri(path).value())
                .body(body)
                .build();
    }
}
