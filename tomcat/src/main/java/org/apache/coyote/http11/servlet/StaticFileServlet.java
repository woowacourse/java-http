package org.apache.coyote.http11.servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.StaticFileResponseUtils;

public class StaticFileServlet implements Servlet {

    @Override
    public boolean canService(HttpRequest request) {
        String resourceFilePath = request.getPath();
        return StaticFileResponseUtils.isExistFile(resourceFilePath);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.sendStaticResource(request.getPath());
    }
}
