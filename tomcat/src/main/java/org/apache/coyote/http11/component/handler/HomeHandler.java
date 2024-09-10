package org.apache.coyote.http11.component.handler;

import org.apache.coyote.http11.component.common.Version;
import org.apache.coyote.http11.component.common.body.TextTypeBody;
import org.apache.coyote.http11.component.request.HttpRequest;
import org.apache.coyote.http11.component.resource.StaticResourceFinder;
import org.apache.coyote.http11.component.response.HttpResponse;
import org.apache.coyote.http11.component.response.ResponseHeader;
import org.apache.coyote.http11.component.response.ResponseLine;
import org.apache.coyote.http11.component.response.StatusCode;

public class HomeHandler implements HttpHandler {

    private static final String HTML_PATH = "/index.html";

    private final StaticResourceFinder resourceFinder;
    private final String uriPath;

    public HomeHandler(final String uriPath) {
        this.resourceFinder = new StaticResourceFinder(HTML_PATH);
        this.uriPath = uriPath;
    }

    @Override
    public String getUriPath() {
        return uriPath;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        return createResponse();
    }

    private HttpResponse createResponse() {
        final var found = new ResponseLine(new Version(1, 1), new StatusCode("OK", 200));
        final var responseHeader = new ResponseHeader();
        responseHeader.put("Content-Type", "text/html;charset=utf-8");
        responseHeader.put("Content-Length", String.valueOf(resourceFinder.getBytes().length));
        final var textTypeBody = new TextTypeBody(new String(resourceFinder.getBytes()));
        return new HttpResponse(found, responseHeader, textTypeBody);
    }
}
