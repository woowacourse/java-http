package org.apache.coyote.http11.handler;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StaticResourceContentType;

public class StaticResourceHandler implements RequestHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        if (!handleable(httpRequest)) {
            return HttpResponse.notFound();
        }
        final String target = httpRequest.getRequestTarget();
        final String responseBody = new String(getResourceByteArray(target));
        final Optional<StaticResourceContentType> findContentType =
            StaticResourceContentType.find(target);

        return findContentType.map(
            contentType -> HttpResponse.ok(
                contentType.convertToHeaderMessage(UTF_8),
                responseBody
            )
        ).orElseGet(HttpResponse::notFound);
    }

    @Override
    public boolean handleable(final HttpRequest httpRequest) {
        return Objects.nonNull(getResource(httpRequest.getRequestTarget()));
    }

    private byte[] getResourceByteArray(final String targetUrl) throws IOException {
        final URL resource = getResource(targetUrl);
        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException("해당 리소스가 존재하지 않습니다. 경로 : " + targetUrl);
        }

        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }

    private URL getResource(final String targetUrl) {
        return getClass().getClassLoader().getResource("static" + targetUrl);
    }
}
