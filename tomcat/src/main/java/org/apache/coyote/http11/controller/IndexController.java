package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.ReasonPhrase;
import org.apache.util.FileReader;

public class IndexController extends AbstractController {
    private static final String CONTENT_TYPE_TEXT_HTML = "text/html;charset=utf-8";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new IOException(ExceptionMessage.NOT_SUPPORT_HTTP_METHOD.getMessage());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        FileReader fileReader = new FileReader();
        final String body = fileReader.readFile("static/index.html");

        response.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
                new ReasonPhrase("OK"));
        response.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_HTML);
        response.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.setHttpBody(new HttpBody(body));
        response.write();
    }
}
