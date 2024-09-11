package org.apache.catalina;

import com.techcourse.controller.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class DefaultController extends AbstractController {

    private static final String PATH_PREFIX = "/";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPath();

        if (path.startsWith(PATH_PREFIX)) {
            response.setStaticResource(path.substring(path.indexOf(PATH_PREFIX) + PATH_PREFIX.length()));
        }
    }
}
