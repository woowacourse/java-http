package nextstep.jwp.controller;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestURI;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseFactory;
import org.apache.coyote.util.FileFinder;

public class ResourceController implements Controller {

    private final List<String> staticResourceRequest = List.of(
        "/login"
    );

    @Override
    public HttpResponse service(final HttpRequest httpRequest) throws IOException {
        return HttpResponseFactory.createStaticResourceHttpResponse(httpRequest);
    }

    @Override
    public boolean canHandle(final HttpRequestURI httpRequestURI) {
        return existsFilePath(httpRequestURI.getPath()) || isStaticResourceRequest(httpRequestURI);
    }

    private boolean existsFilePath(final String pathString) {
        try {
            FileFinder.readFile(pathString);
            return true;
        } catch (NoSuchElementException | IOException e) {
            return false;
        }
    }

    private boolean isStaticResourceRequest(final HttpRequestURI httpRequestURI) {
        return staticResourceRequest.stream()
            .anyMatch(httpRequestURI::hasSamePath);
    }
}
