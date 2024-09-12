package org.apache.coyote.http11.servlet;

import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.view.View;
import org.apache.coyote.http11.util.StaticFileUtils;

public class StaticFileServlet implements Servlet {
    @Override
    public Optional<View> service(HttpRequest request) {
        return Optional.empty();
    }

    @Override
    public boolean canService(HttpRequest request) {
        return StaticFileUtils.isExistStaticFile(request.getPath());
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.sendStaticResource(request.getPath());
    }
}
