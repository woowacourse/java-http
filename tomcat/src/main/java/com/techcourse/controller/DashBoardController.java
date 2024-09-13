package com.techcourse.controller;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import com.techcourse.http.MimeType;
import java.io.IOException;
import org.apache.catalina.StaticResourceProvider;
import jakarta.servlet.http.AbstractController;

public class DashBoardController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setBody(StaticResourceProvider.getStaticResource("/index.html"))
                .setContentType(MimeType.HTML.getMimeType());
    }
}
