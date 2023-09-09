package org.apache.coyote.http11;

import static org.apache.coyote.http11.ContentType.TEXT_CSS;
import static org.apache.coyote.http11.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.HttpStatus.OK;

import java.util.Optional;

public class ResourceController extends AbstractController {

    private final ResourceLoader resourceLoader;

    public ResourceController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        Optional<String> acceptHeader = request.getHeader("Accept");
        if (acceptHeader.isPresent() && acceptHeader.get().contains("text/css")) {
            return HttpResponse.status(OK)
                    .body(resourceLoader.load("static" + request.uri()))
                    .contentType(TEXT_CSS)
                    .build();
        }
        return HttpResponse.status(OK)
                .body(resourceLoader.load("static" + request.uri()))
                .contentType(TEXT_HTML)
                .build();
    }
}
