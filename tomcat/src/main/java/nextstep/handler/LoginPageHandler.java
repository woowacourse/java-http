package nextstep.handler;

import java.io.IOException;
import nextstep.handler.util.ResourceProcessor;
import org.apache.coyote.Handler;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HttpMethod;

public class LoginPageHandler implements Handler {

    private final String path;
    private final String targetResourceName;

    public LoginPageHandler(final String path, final String targetResourceName) {
        this.path = path;
        this.targetResourceName = targetResourceName;
    }

    @Override
    public boolean supports(final Request request, final String contextPath) {
        return isGetMethod(request) && isLoginPageRequest(request, contextPath);
    }

    private boolean isGetMethod(final Request request) {
        return request.matchesByMethod(HttpMethod.GET);
    }

    private boolean isLoginPageRequest(final Request request, final String contextPath) {
        return request.matchesByPathExcludingContextPath(path, contextPath);
    }

    @Override
    public Response service(final Request request, final String staticResourcePath) throws IOException {
        final String resourceFullName = staticResourcePath + targetResourceName;
        final String responseBody = ResourceProcessor.readResourceFile(resourceFullName);
        final ContentType contentType = ResourceProcessor.findContentType(request, resourceFullName);

        return Response.of(request, HttpStatusCode.OK, contentType, responseBody);
    }
}
