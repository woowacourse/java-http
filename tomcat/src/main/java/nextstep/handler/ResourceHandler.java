package nextstep.handler;

import java.io.IOException;
import nextstep.handler.util.ResourceProcessor;
import org.apache.coyote.Handler;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HttpMethod;

public class ResourceHandler implements Handler {

    @Override
    public boolean supports(final Request request, final String ignoredRootContextPath) {
        return isGetMethod(request) && isStaticResourceRequest(request);
    }

    private boolean isGetMethod(final Request request) {
        return request.matchesByMethod(HttpMethod.GET);
    }

    private boolean isStaticResourceRequest(final Request request) {
        return request.isStaticResource();
    }

    @Override
    public Response service(final Request request, final String staticResourcePath) throws IOException {
        final String resourceFullName = staticResourcePath + request.url();
        final String responseBody = ResourceProcessor.readResourceFile(resourceFullName);
        final ContentType contentType = ResourceProcessor.findContentType(request, resourceFullName);

        return Response.of(request, HttpStatusCode.OK, contentType, responseBody);
    }
}
