package org.apache.coyote;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.apache.coyote.http11.HttpHeaderType.ACCEPT;
import static org.apache.coyote.http11.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.response.HttpStatusCode.NOT_FOUND;
import static org.apache.coyote.http11.response.HttpStatusCode.OK;

public class ResourceController extends Controller {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return "GET".equals(httpRequest.getMethod());
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final String resourceUrl = httpRequest.getTarget();
        String contentType = "text/html;charset=utf-8";

        final String acceptHeader = httpRequest.getHeaders().getHeaderValue(ACCEPT);
        if (acceptHeader != null) {
            contentType = acceptHeader.split(",")[0];
        }

        URL resource = getClass().getClassLoader().getResource("static/" + resourceUrl);
        if (resource != null) {
            httpResponse.setStatusCode(OK);
        } else {
            resource = getClass().getClassLoader().getResource("static/" + "404.html");
            httpResponse.setStatusCode(NOT_FOUND);
            contentType = "text/html;charset=utf-8";
        }

        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        httpResponse.addHeader(CONTENT_TYPE, contentType);
        httpResponse.setBody(responseBody);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
    }
}
