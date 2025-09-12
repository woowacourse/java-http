package com.techcourse.controller;

import java.io.IOException;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Status;

public class RootController extends AbstractController {

    @Override
    protected Http11Response doGet(Http11Request http11Request) throws IOException {
        String response = "Hello world!";

        byte[] body = response.getBytes();

        return new Http11Response(body,"text/html", Http11Status.OK);
    }
}
