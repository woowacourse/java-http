package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.util.FileReader;

public class ChartPieController extends AbstractController {
    private static final String CONTENT_TYPE_TEXT_JAVASCRIPT = "text/javascript;charset=utf-8";
    private static final String CONTENT_TYPE = "Content-Type";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new IOException(ExceptionMessage.NOT_SUPPORT_HTTP_METHOD.getMessage());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final String body = new FileReader().readFile("static/assets/chart-pie.js");

        response.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_JAVASCRIPT);
        response.setHttpBody(new HttpBody(body));
        response.write();
    }
}
