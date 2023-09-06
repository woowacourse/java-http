package org.apache.coyote.handler;

import java.io.IOException;
import org.apache.coyote.Handler;
import org.apache.coyote.handler.util.ResourceProcessor;
import org.apache.coyote.http.HttpSession;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpHeaderConsts;
import org.apache.coyote.http.util.HttpMethod;

public class RegisterPageHandler implements Handler {

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
                        new HeaderDto(HttpHeaderConsts.LOCATION, "/index.html")
                );
            }
        }

        final String resourceFullName = prefix + resourceName;
        final String responseBody = ResourceProcessor.readResourceFile(resourceFullName);
        final ContentType contentType = ResourceProcessor.findContentType(request, resourceFullName);

        return Response.of(request, HttpStatusCode.OK, contentType, responseBody);
    }
}
