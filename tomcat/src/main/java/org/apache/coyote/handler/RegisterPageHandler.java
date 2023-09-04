package org.apache.coyote.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.Handler;
import org.apache.coyote.http.HttpSession;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.HttpHeaderConsts;
import org.apache.coyote.http.util.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterPageHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(RegisterPageHandler.class);

    private final String path;
    private final String rootContextPath;
    private final String resourceName;
    private final String prefix;

    public RegisterPageHandler(
            final String path,
            final String rootContextPath,
            final String resourceName,
            final String prefix
    ) {
        this.path = path;
        this.rootContextPath = rootContextPath;
        this.resourceName = resourceName;
        this.prefix = prefix;
    }

    @Override
    public boolean supports(final Request request) {
        return isGetMethod(request) && isRegisterPageRequest(request);
    }

    private boolean isGetMethod(final Request request) {
        return request.matchesByMethod(HttpMethod.GET);
    }

    private boolean isRegisterPageRequest(final Request request) {
        return request.matchesByPath(path, rootContextPath) && !request.hasQueryParameters();
    }

    @Override
    public Response service(final Request request) throws IOException {
        final HttpSession session = request.getSession(false);

        if (session != null) {
            final Object user = session.getAttribute(LoginHandler.ACCOUNT_KEY);

            if (user != null) {
                return Response.of(
                        request,
                        HttpStatusCode.FOUND,
                        ContentType.JSON,
                        user.toString(),
                        request.getCookie(),
                        new HeaderDto("Location", "/index.html")
                );
            }
        }

        try {
            final var resource = ClassLoader.getSystemClassLoader()
                                            .getResource(prefix + resourceName);
            final var path = new File(resource.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));
            final String accept = findAcceptHeader(request);
            final ContentType contentType = ContentType.findContentType(accept, resourceName);

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
