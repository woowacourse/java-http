package org.apache.coyote.handler;

import java.nio.file.Path;

import org.apache.coyote.file.FileContentParser;
import org.apache.coyote.file.FilePathParser;
import org.apache.http.header.HttpHeaderName;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class StaticResourceHandler implements Controller {

    private static final StaticResourceHandler INSTANCE = new StaticResourceHandler();

    private StaticResourceHandler() {
    }

    public static StaticResourceHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponse(handle(request));
    }

    public HttpResponse handle(final HttpRequest httpRequest) {
        final String requestPath = httpRequest.getPath();
        final String resourcePath = FilePathParser.findResourcePath(requestPath);
        final Path physicalPath = Path.of(getClass().getClassLoader().getResource(resourcePath).getPath());

        final String fileContent = FileContentParser.parseContent(physicalPath);
        final String mimeType = FileContentParser.parseContentType(physicalPath);

        return HttpResponse.builder()
                .body(fileContent)
                .addHeader(HttpHeaderName.CONTENT_TYPE, mimeType)
                .build();
    }
}
