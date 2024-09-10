package org.apache.coyote.http11.component.handler;

import org.apache.coyote.http11.component.common.body.TextTypeBody;
import org.apache.coyote.http11.component.request.HttpRequest;
import org.apache.coyote.http11.component.resource.StaticResourceFinder;
import org.apache.coyote.http11.component.response.HttpResponse;
import org.apache.coyote.http11.component.response.ResponseHeader;
import org.apache.coyote.http11.component.response.ResponseLine;

public class StaticResourceHandler implements HttpHandler {

    private final String resourcePath;
    private final StaticResourceFinder resourceFinder;

    public StaticResourceHandler(final String resourcePath) {
        this.resourcePath = resourcePath;
        this.resourceFinder = new StaticResourceFinder(resourcePath);
    }

    @Override
    public String getUriPath() {
        return resourcePath;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final var ok = ResponseLine.OK;
        final var responseHeader = new ResponseHeader();
        responseHeader.put("Content-Length", String.valueOf(resourceFinder.getBytes().length));
        responseHeader.put("Content-Type", MimeType.find(resourcePath) + ";charset=utf-8");
        final var textTypeBody = new TextTypeBody(new String(resourceFinder.getBytes()));
        return new HttpResponse(ok, responseHeader, textTypeBody);
    }
}
