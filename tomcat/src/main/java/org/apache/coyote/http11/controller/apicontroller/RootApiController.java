package org.apache.coyote.http11.controller.apicontroller;

import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public class RootApiController extends AbstractController {

    private static final Pattern ROOT_URI_PATTERN = Pattern.compile("/");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchRequestLine(HttpMethod.GET, ROOT_URI_PATTERN);
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.ok("Hello world!")
                .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {

    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {

    }
}
