package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceHandler implements RequestHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        if (!handleable(httpRequest)) {
            return HttpResponse.notFound();
        }

        final String requestTarget = httpRequest.getRequestTarget();
        final String body = new String(getResourceByteArray(requestTarget));
        final Optional<ContentType> findContentType = ContentType.find(requestTarget);

        return createResponse(findContentType, body);
    }

    @Override
    public boolean handleable(final HttpRequest httpRequest) {
        return Objects.nonNull(getResource(httpRequest.getRequestTarget()));
    }

    private URL getResource(final String targetUrl) {
        return getClass().getClassLoader().getResource("static" + targetUrl);
    }

    private byte[] getResourceByteArray(final String targetUrl) throws IOException {
        final URL resource = getResource(targetUrl);

        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }

    private HttpResponse createResponse(
        final Optional<ContentType> contentType,
        final String body
    ) {
        if (contentType.isEmpty()) {
            return HttpResponse.notFound();
        }
        final ContentType responseContentType = contentType.get();
        if (responseContentType == ContentType.HTML) {
            return HttpResponse.ok(ContentType.HTML_UTF8, body);
        }
        return HttpResponse.ok(responseContentType, body);
    }
}
