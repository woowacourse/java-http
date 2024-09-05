package com.techcourse.controller.page;

import java.io.IOException;
import org.apache.coyote.http11.HttpProtocol;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.request.line.Uri;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.util.FileUtils;

public class LoginPageController implements HttpRequestHandler {

    private static final String PAGE_RESOURCE_PATH = "/login.html";
    private static final Method SUPPORTING_METHOD = Method.GET;
    private static final Uri SUPPORTING_URI = new Uri("/login");
    private static final HttpProtocol SUPPORTING_PROTOCOL = HttpProtocol.HTTP_11;

    @Override
    public boolean supports(final HttpRequest request) {
        if (request.isMethodNotEqualWith(SUPPORTING_METHOD)) {
            return false;
        }
        if (request.isHttpProtocolNotEqualWith(SUPPORTING_PROTOCOL)) {
            return false;
        }
        if (request.isUriNotEqualWith(SUPPORTING_URI)) {
            return false;
        }
        return true;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        String fileContent = FileUtils.readFile(PAGE_RESOURCE_PATH);
        return HttpResponse.ok(fileContent, "html");
    }
}
