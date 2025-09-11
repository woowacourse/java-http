package org.apache.catalina.controller.root;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(
            final Http11Request request,
            final Http11Response response
    ) {
        response.setBody("Hello world!", "text/html;charset=utf-8");
    }
}
