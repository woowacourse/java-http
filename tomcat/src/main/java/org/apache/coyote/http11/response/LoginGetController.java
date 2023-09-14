package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;

public class LoginGetController implements Controller {

    @Override
    public void service(final HttpRequest request,
                        final HttpResponse response) {
        final String resourcePath = request.getRequestLine().getRequestUrl() + ".html";
        final String responseBody = ResourceResolver.resolve(resourcePath);

        if (request.isHasValidatedSession()) {
            final String jSessionId = request.getJSessionId();
            response.addJSessionId(jSessionId);
            response.setStatusCode(StatusCode.FOUND);
            response.addHeader("Location", "/index.html");
            return;
        }
        response.setStatusCode(StatusCode.OK);
        response.setResponseBody(ResourceResolver.resolve(resourcePath));
        response.addHeader("Content-Type", ContentType.from(resourcePath).getContentType() + ";charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

}
