package org.apache.catalina.servlet;

import org.apache.catalina.servlet.exception.MethodNotAllowedException;
import org.apache.catalina.servlet.exception.NotFoundFileException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new MethodNotAllowedException();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String requestUri = request.getRequestUri();
        StaticResource staticResource;
        try {
            staticResource = StaticResource.of(requestUri);
        } catch (NotFoundFileException e) {
            staticResource = StaticResource.of("/404.html");
        }
        ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
        response.setResponseBody(responseBody);
    }
}
