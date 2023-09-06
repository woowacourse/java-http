package nextstep.jwp.controller;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestURI;
import org.apache.coyote.http11.ResponseEntityFactory;
import org.apache.coyote.util.FileFinder;

public class ResourceController implements Controller {

    private final List<String> staticResourceRequest = List.of(
        "/login",
        "/register"
    );

    @Override
    public ResponseEntity service(final HttpRequest httpRequest) throws IOException {
        return ResponseEntityFactory.createStaticResourceHttpResponse(httpRequest);
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return existsFilePath(httpRequest.getPath()) || isStaticResourceRequest(httpRequest);
    }

    private boolean existsFilePath(final String pathString) {
        try {
            FileFinder.readFile(pathString);
            return true;
        } catch (NoSuchElementException | IOException e) {
            return false;
        }
    }

    private boolean isStaticResourceRequest(final HttpRequest httpRequest) {
        final HttpRequestURI httpRequestURI = httpRequest.getRequestURI();
        final boolean isStaticResourceRequest = staticResourceRequest.stream()
            .anyMatch(httpRequestURI::hasSamePath);
        final boolean isGetMethod = httpRequest.isSameMethod(HttpMethod.GET);

        return isStaticResourceRequest && isGetMethod;
    }
}
