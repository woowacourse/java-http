package org.apache.coyote.http11.resource;

import java.io.IOException;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.data.ContentType;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.HttpStatusCode;
import org.apache.coyote.http11.data.MediaType;

public class ResourceHandler implements Controller {
    private static final ResourceHandler INSTANCE = new ResourceHandler();

    private final ResourceReader resourceReader = ResourceReader.getInstance();

    private ResourceHandler() {
    }

    public static ResourceHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String path = httpRequest.getPath();
        if (path.equals("/")) {
            getRoot(httpResponse);
        } else if (path.endsWith("html")) {
            getHtml(path, httpResponse);
        } else if (path.endsWith("css")) {
            getCss(path, httpResponse);
        } else if (path.endsWith("js")) {
            getJs(path, httpResponse);
        }
    }

    public HttpResponse getRoot(HttpResponse httpResponse) {
        httpResponse.addContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                .addHttpStatusCode(HttpStatusCode.OK)
                .addResponseBody("Hello world!");
        return httpResponse;
    }

    public HttpResponse getHtml(String path, HttpResponse httpResponse) throws IOException {
        String responseBody = resourceReader.loadResourceAsString(path);
        httpResponse.addContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                .addHttpStatusCode(HttpStatusCode.OK)
                .addResponseBody(responseBody);
        return httpResponse;
    }

    public HttpResponse getCss(String path, HttpResponse httpResponse) throws IOException {
        String responseBody = resourceReader.loadResourceAsString(path);
        httpResponse.addContentType(new ContentType(MediaType.CSS, null))
                .addHttpStatusCode(HttpStatusCode.OK)
                .addResponseBody(responseBody);
        return httpResponse;
    }

    public HttpResponse getJs(String path, HttpResponse httpResponse) throws IOException {
        String responseBody = resourceReader.loadResourceAsString(path);
        httpResponse.addContentType(new ContentType(MediaType.JAVASCRIPT, null))
                .addHttpStatusCode(HttpStatusCode.OK)
                .addResponseBody(responseBody);
        return httpResponse;
    }
}
