package com.techcourse.controller;

import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.MimeType;
import java.io.IOException;
import org.apache.catalina.StaticResourceProvider;
import org.apache.coyote.http11.AbstractController;

public class DashBoardController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setBody(StaticResourceProvider.getStaticResource("/index.html"))
                .setContentType(MimeType.HTML.getMimeType());
    }
}
