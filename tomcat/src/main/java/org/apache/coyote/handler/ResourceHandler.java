package org.apache.coyote.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Handler;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.HttpHeaderConsts;
import org.apache.coyote.http.util.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(ResourceHandler.class);

    private final String prefix;

    public ResourceHandler(final String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean supports(final Request request) {
        return isGetMethod(request) && isStaticResourceRequest(request);
    }

    private boolean isGetMethod(final Request request) {
        return request.matchesByMethod(HttpMethod.GET);
    }

    private boolean isStaticResourceRequest(final Request request) {
        return request.isStaticResource();
    }

    @Override
    public Response service(final Request request) throws IOException {
        try {
            final String resourceUrlPatterns = request.url();
            final URL resource = ClassLoader.getSystemClassLoader()
                                            .getResource(prefix + resourceUrlPatterns);
            final File resourceFile = new File(resource.getPath());
            final Path path = resourceFile.toPath();
            final String responseBody = new String(Files.readAllBytes(path));
            final String accept = findAcceptHeader(request);
            final ContentType contentType = ContentType.findContentType(accept, resourceUrlPatterns);

            return Response.of(request, HttpStatusCode.OK, contentType, responseBody);
        } catch (NullPointerException ex) {
            log.info("css not found : ", ex);

            return Response.of(request, HttpStatusCode.NOT_FOUND, ContentType.JSON, HttpConsts.BLANK);
        }
    }

    private String findAcceptHeader(final Request request) {
        final String accept = request.findHeaderValue(HttpHeaderConsts.ACCEPT);

        if (accept == null) {
            return HttpConsts.BLANK;
        }

        return accept;
    }
}
