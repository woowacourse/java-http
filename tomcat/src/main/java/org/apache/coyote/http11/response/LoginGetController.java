package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.catalina.session.SessionManager;

public class LoginGetController implements Controller {

    @Override
    public void service(final HttpRequest request,
                        final HttpResponse response) {
        final String resourcePath = request.getRequestLine().getRequestUrl() + ".html";
        final String responseBody = ResourceResolver.resolve(resourcePath);

        if (request.hasJSessionId() && (SessionManager.isExist(request.getJSessionId()))) {
            response.setStatusCode(StatusCode.OK);
            response.setResponseBody(ResourceResolver.resolve("/index.html"));
            response.addHeader("Content-Type", ContentType.from("/index.html").getContentType() + ";charset=utf-8");
            response.addHeader("Content-Length", String.valueOf(ResourceResolver.resolve("/index.html").getBytes().length));
            return;
        }

        response.setStatusCode(StatusCode.OK);
        response.setResponseBody(ResourceResolver.resolve(resourcePath));
        response.addHeader("Content-Type", ContentType.from(resourcePath).getContentType() + ";charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

}
