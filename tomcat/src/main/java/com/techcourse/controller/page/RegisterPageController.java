package com.techcourse.controller.page;

import java.io.IOException;
import org.apache.coyote.http11.HttpProtocol;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.request.line.Uri;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.util.FileUtils;

public class RegisterPageController implements HttpRequestHandler {

    private static final String PAGE_RESOURCE_PATH = "/register.html";
    private static final Method SUPPORTING_METHOD = Method.GET;
    private static final Uri SUPPORTING_URI = new Uri("/register");
    private static final HttpProtocol SUPPORTING_PROTOCOL = HttpProtocol.HTTP_11;

    @Override
    public boolean supports(HttpRequest request) {
        return request.methodEquals(SUPPORTING_METHOD) &&
                request.protocolEquals(SUPPORTING_PROTOCOL) &&
                request.uriEquals(SUPPORTING_URI);
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String fileContent = FileUtils.readFile(PAGE_RESOURCE_PATH);
        return HttpResponse.ok(fileContent, "html");
    }
}
