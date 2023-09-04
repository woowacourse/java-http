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

public class LoginPageHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginPageHandler.class);

    private final String path;
    private final String rootContextPath;
    private final String targetResourceName;
    private final String prefix;

    public LoginPageHandler(
            final String path,
            final String rootContextPath,
            final String targetResourceName,
            final String prefix
    ) {
        this.path = path;
        this.rootContextPath = rootContextPath;
        this.targetResourceName = targetResourceName;
        this.prefix = prefix;
    }

    @Override
    public boolean supports(final Request request) {
        return isGetMethod(request) && isLoginPageRequest(request);
    }

    private boolean isGetMethod(final Request request) {
        return request.matchesByMethod(HttpMethod.GET);
    }

    private boolean isLoginPageRequest(final Request request) {
        return request.matchesByPath(path, rootContextPath) && !request.hasQueryParameters();
    }

    @Override
    public Response service(final Request request) throws IOException {

        try {
            final URL resource = ClassLoader.getSystemClassLoader()
                                            .getResource(prefix + targetResourceName);
            final Path path = new File(resource.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));
            final String accept = findAcceptHeader(request);
            final ContentType contentType = ContentType.findContentType(accept, targetResourceName);

            return Response.of(request, HttpStatusCode.OK, contentType, responseBody);
        } catch (NullPointerException ex) {
            log.info("page not found : ", ex);

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
