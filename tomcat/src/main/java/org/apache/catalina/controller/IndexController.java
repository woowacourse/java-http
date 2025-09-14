package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.StaticResource;
import org.apache.coyote.http11.StaticResourceProvider;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class IndexController extends AbstractController {

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        final StaticResource staticResource = StaticResourceProvider.getStaticResource("/index.html");
        response.setStaticResource(staticResource);
        response.setHttpStatus(HttpStatus.OK);
    }
}
